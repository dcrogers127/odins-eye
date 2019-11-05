package actors

import akka.actor.{Actor, Props}
import model.Game
import dao.InMemoryReadDao

class InMemoryReadActor(games: Seq[Game])
  extends Actor {
  import InMemoryReadActor._

  val gameDao = new InMemoryReadDao(games)

  override def receive: Receive = {
    case InitializeState => gameDao.init
    case GetGames => sender() ! gameDao.getGames
  }
}

object InMemoryReadActor {
  case object InitializeState
  case object GetGames

  val name = "in-memory-read-actor"
  val path = s"/user/$name"
  def props(games: Seq[Game]) = Props(new InMemoryReadActor(games))
}

