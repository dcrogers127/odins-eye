package dao

import model.LogRecord
import model.Game

class InMemoryReadDao(records: Seq[LogRecord]) {
  import scala.collection.mutable.{Map => MMap}
  val games = MMap.empty[Int, Game]

  def init(): Unit = records.foreach(processEvent)

  def processEvent(record: LogRecord): Unit = {
    record.action match {
      case _ => ()
    }
  }

  def getGames: Seq[Game] = {
    games.values.toList.sortWith(_.game_date < _.game_date)
  }
}

