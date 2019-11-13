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
        games += (event.game.game_id -> Game(
          event.game.game_id,
          event.game.game_date,
          event.game.start_time,
          event.game.visitor,
          "",
          event.game.home,
          ""
        ))
    }
  }

  def getGames: Seq[Game] = {
    games.values.toList.sortWith(_.game_id < _.game_id)
  }
}
