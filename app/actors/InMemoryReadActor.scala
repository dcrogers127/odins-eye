package actors

import akka.actor.{Actor, Props}
import model.{Game, LogRecord}
import dao.InMemoryReadDao

class InMemoryReadActor(games: Seq[Game])
  extends Actor {
  import InMemoryReadActor._

  val gameDao = new InMemoryReadDao(games)

  override def receive: Receive = {
    case InitializeState => gameDao.init
    case GetGames(maybeStartDate, maybeEndDate) => sender() ! gameDao.getGames(maybeStartDate, maybeEndDate)
    case GetAllGames => sender() ! gameDao.getAllGames
    // case ProcessEvent(event) => sender() ! gameDao.processEvent(event)
  }
}

object InMemoryReadActor {
  case class ProcessEvent(event: LogRecord)
  case object InitializeState
  case class GetGames(maybeStartDate: Option[String], maybeEndDate: Option[String])
  case object GetAllGames

  val name = "in-memory-read-actor"
  val path = s"/user/$name"
  def props(games: Seq[Game]) = Props(new InMemoryReadActor(games))
}

