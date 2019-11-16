package dao

import java.text.SimpleDateFormat

import model.{Game, GameBase, GameCSV, Score}
import fileio.RowParser.rowParserFor
import scalikejdbc._
import au.com.bytecode.opencsv._

import scala.collection.JavaConverters._
import scala.util.{Success, Try}

class GameDao {
  private val dataPath = "public/data/games.csv"
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  private val brDataFormat = new SimpleDateFormat("E MMM dd yyyy")

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
}
