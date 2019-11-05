package dao
import model.Game

class InMemoryReadDao(sGames: Seq[Game]) {
  import scala.collection.mutable.{Map => MMap}
  val games = MMap.empty[Int, Game]

  def init(): Unit = sGames.foreach(processGames)

  def processGames(game: Game): Unit = {
    games += (game.id -> game)
  }

  def getGames: Seq[Game] = {
    games.values.toList.sortWith(_.game_date < _.game_date)
  }
}
