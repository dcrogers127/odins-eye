package dao
import util.Common.{brWebDataFormat, dateFormat, monthNumToStr, teamToTm}
import model.ScheduleElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import play.api.Logger

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

class BBallRefDao {
  val log = Logger(this.getClass)
  private val sleepSecs = 3

  def extractScheduleByMonth(year: String, month: String): Seq[ScheduleElement] = {
    Thread.sleep(sleepSecs*1000)
    val bBallRefUrl = s"https://www.basketball-reference.com/leagues/NBA_${year}_games-$month.html"
    val tryDoc = Try(Jsoup.connect(bBallRefUrl).get())
    tryDoc match {
      case Success(doc) =>
        val schedule = doc.getElementById("schedule")
        val rows = schedule.select("tr")

        val cleanElements: ArrayBuffer[ScheduleElement] = ArrayBuffer()
        var numFailures = 0
        rows.forEach{ r =>
          extractScheduleData(r) match {
            case Success(g) => cleanElements += g
            case Failure(_) => numFailures += 1
          }
        }
        log.info(s"Extracted ${cleanElements.size} games with $numFailures failures.")
        cleanElements
      case Failure(_) => Seq()
    }
  }

  def getSchedule(year: String): Seq[ScheduleElement] = {
    monthNumToStr.values.flatMap(extractScheduleByMonth(year, _)).toSeq
  }

  private def extractScheduleData(e: Element): Try[ScheduleElement] = {
    Try{
      val td = e.select("td")
      val gameDate = brWebDataFormat.parse(e.select("th").first().text())
      val awayTeam = td.eq(1).text()
      val homeTeam = td.eq(3).text()
      val awayTm = teamToTm(awayTeam)
      val homeTm = teamToTm(homeTeam)

      ScheduleElement(
        s"${dateFormat.format(gameDate)}_${awayTm}_$homeTm", // gameId
        gameDate, // gameDate
        td.eq(0).text(), // startEt
        awayTeam, // awayTeam
        awayTm, // awayTm
        Try(td.eq(2).text().toInt).toOption, // awayPoints
        homeTeam, // homeTeam
        homeTm, // homeTm
        Try(td.eq(4).text().toInt).toOption, // homePoints
        td.eq(6).text(), // overtime
        td.eq(7).text(), // attendance
        td.eq(8).text(), // notes
        td.eq(5).select("a").attr("href") // boxScoreUrl
      )
    }
  }


}
