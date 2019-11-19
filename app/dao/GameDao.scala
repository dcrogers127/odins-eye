package dao

import java.text.SimpleDateFormat

import model._
import fileio.RowParser.rowParserFor
import scalikejdbc._
import au.com.bytecode.opencsv._
import common.Common._
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import play.api.Logger

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

class GameDao {
  val log = Logger(this.getClass)

  private val dataPath = "public/data/games.csv"
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  private val brDataFormat = new SimpleDateFormat("E MMM dd yyyy")
  private val brWebDataFormat = new SimpleDateFormat("E, MMM dd, yyyy")
  private val sleepSecs = 3

  def processGameCSV: List[GameBase] = {
    val reader = new CSVReader(new java.io.FileReader(dataPath))
    val rows = reader.readAll.asScala
      .map(row => rowParserFor[GameCSV](row.toList))
      .toList
      .flatMap(_.toOption)
    rows.map {
      case GameCSV(game_date_str, start_et, visitor, _, home, _, _, _, _, _ ) => {
        val game_date = brDataFormat.parse(game_date_str)
        val gameDateStr = dateFormat.format(game_date)
        GameBase(
          s"$gameDateStr-$visitor-$home",
          game_date,
          start_et,
          visitor,
          home
        )
      }
    }
  }

  def insertGame(game: GameBase): Unit = {
    NamedDB('statsstore).localTx{ implicit session =>
      sql"""insert into games (
            game_id,
            game_date,
            start_time,
            visitor,
            home
        ) values (
           ${game.game_id},
           ${game.game_date},
           ${game.start_time},
           ${game.visitor},
           ${game.home}
     )""".update().apply()
    }
  }

  def updateScore(score: Score): Unit = {
    NamedDB('statsstore).localTx{ implicit session =>
      sql"""update games
        set visitor_pts = ${score.visitor_pts},
           home_pt = ${score.home_pts}
           ot = '${score.ot}'
        where game_id = '${score.game_id}'
       """.update().apply()
    }
  }

  def batchInsertGames: Unit = {
    val reader = new CSVReader(new java.io.FileReader(dataPath))
    val rows = reader.readAll.asScala.map(row => rowParserFor[GameCSV](row.toList))
    NamedDB('statsstore).localTx{ implicit session =>
      sql"delete from stg_games".update().apply()
      for (row <- rows) {
        row match {
          case Success(GameCSV(
          game_date,
          start_et,
          visitor,
          visitor_pts,
          home,
          home_pts,
          box_score_url,
          ot,
          attend,
          notes
          )) => sql"""insert into stg_games (
             game_date,
             start_et,
             visitor,
             visitor_pts,
             home,
             home_pts,
             box_score_url,
             ot,
             attend,
             notes
           ) values (
             $game_date,
             $start_et,
             $visitor,
             $visitor_pts,
             $home,
             $home_pts,
             $box_score_url,
             $ot,
             $attend,
             $notes
           )""".update().apply()
          case _ => Unit
        }
      }
    }
  }

  def initGames: Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"select * from games".map(Game.fromRS).list().apply()
    }
  }

  private def extractScheduleData(e: Element): Try[ScheduleElement] = {
    Try{
      val td = e.select("td")
      val awayTeam = td.eq(1).text()
      val homeTeam = td.eq(3).text()
      ScheduleElement(
        brWebDataFormat.parse(e.select("th").first().text()), // gameDate
        td.eq(0).text(), // startEt
        awayTeam, // awayTeam
        teamToTm(awayTeam), // awayTm
        Try(td.eq(2).text().toInt).toOption, // awayPoints
        homeTeam, // HomeTeam
        teamToTm(homeTeam), // HomeTm
        Try(td.eq(4).text().toInt).toOption, // homePoints
        td.eq(6).text(), //overtime
        td.eq(7).text(),
        td.eq(8).text(),
        td.eq(5).select("a").attr("href")
      )
    }
  }

  def extractScheduleByMonth(year: String, month: String, sleepSecs: Int): Seq[ScheduleElement] = {
    Thread.sleep(sleepSecs*1000)
    val bBallRefUrl = s"https://www.basketball-reference.com/leagues/NBA_${year}_games-${month}.html"
    val tryDoc = Try(Jsoup.connect(bBallRefUrl).get())
    tryDoc match {
      case Success(doc) =>
        val schedule = doc.getElementById("schedule")
        val rows = schedule.select("tr")

        val cleanElements: ArrayBuffer[ScheduleElement] = ArrayBuffer()
        var numFailures = 0
        rows.forEach{ r =>
          extractScheduleData(r) match {
            case Success(_) => cleanElements += _
            case Failure(_) => numFailures += 1
          }
        }
        log.info(s"Extracted ${cleanElements.size} games with $numFailures failures.")
        cleanElements
      case Failure(_) => Seq()
    }
  }

  def getSchedule(year: String): Seq[ScheduleElement] = {
    monthNumToStr.values.flatMap(extractScheduleByMonth(year, _, sleepSecs)).toSeq
  }

  def cleanGameDB: Unit = {
    NamedDB('statsstore).localTx { implicit session =>
      sql"delete from stg_games".update().apply()
    }
  }

  def initGameDB: Unit = {
    val games = getSchedule(currentYear)
    NamedDB('statsstore).localTx{ implicit session =>
      for (game <- games) {
        sql"""insert into games (
          game.gameDate,
          game.startEt,
          game.awayTeam,
          game.awayTm,
          game.awayPoints,
          game.homeTeam,
          game.homeTm,
          game.homePoints,
          game.overtime,
          game.attendance,
          game.notes,
          game.boxScoreUrl
        ) values (
          ${game.gameDate},
          ${game.startEt},
          ${game.awayTeam},
          ${game.awayTm},
          ${game.awayPoints.getOrElse("NULL")},
          ${game.homeTeam},
          ${game.homeTm},
          ${game.homePoints.getOrElse("NULL")},
          ${game.overtime},
          ${game.attendance},
          ${game.notes},
          ${game.boxScoreUrl}
        )""".update().apply()
      }
    }
  }
}
