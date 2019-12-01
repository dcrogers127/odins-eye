package dao
import java.text.SimpleDateFormat
import java.util.Date

import events.gameInfo.GameCreated
import model.{Game, LogRecord}

import scala.util.Try

class InMemoryReadDao(sGames: Seq[Game]) {
  import scala.collection.mutable.{Map => MMap}
  val games = MMap.empty[String, Game]
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  def init(): Unit = sGames.foreach(processGames)

  def processGames(game: Game): Unit = {
    games += (game.gameId -> game)
  }

  /*
  def processEvent(record: LogRecord): Unit = {
    record.action match {
      case GameCreated.actionName =>
        val event = record.data.as[GameCreated]
        val game = Game(
          event.game_id,
          event.game_date.toString,
          event.start_time,
          event.visitor,
          "",
          event.home,
          ""
        )
        games += (event.game_id -> game)
    }
  }
  */
  def convertToDate(maybeDate: Option[String]): Option[Date] =
    maybeDate.flatMap(x => Try(dateFormat.parse(x)).toOption)

  def getGames(maybeStartDate: Option[String], maybeEndDate: Option[String]): Seq[Game] = {
    val mStartDate = convertToDate(maybeStartDate)
    val mEndDate = convertToDate(maybeEndDate)
    (mStartDate, mEndDate) match {
      case (Some(startDate), Some(endDate)) =>
        if (startDate.after(endDate)) Seq()
        else {
          val strStartDate = dateFormat.format(startDate)
          val strEndDate = dateFormat.format(endDate)
          games.values
            .filter(game => game.gameDate >= strStartDate &  game.gameDate <= strEndDate)
            .toList.sortWith(_.gameId < _.gameId)
        }
      case _ => Seq()
    }
  }

  def getAllGames: Seq[Game] = games.values.toList.sortWith(_.gameId < _.gameId)
}
