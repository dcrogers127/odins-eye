package dao
import events.gameInfo.GameCreated
import model.{Game, LogRecord}

class InMemoryReadDao(sGames: Seq[Game]) {
  import scala.collection.mutable.{Map => MMap}
  val games = MMap.empty[String, Game]

  def init(): Unit = sGames.foreach(processGames)

  def processGames(game: Game): Unit = {
    games += (game.game_id -> game)
  }

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

  def getGames: Seq[Game] = {
    games.values.toList.sortWith(_.game_id < _.game_id)
  }
}
