package actors

import akka.actor.{Actor, Props}
import model.LogRecord
import dao.InMemoryReadDao

class InMemoryReadActor(logRecords: Seq[LogRecord])
  extends Actor {
  import InMemoryReadActor._

  val readDao = new InMemoryReadDao(logRecords)

  override def receive: Receive = {
    case InitializeState => readDao.init()
    case GetGames => sender() ! readDao.getGames
    case ProcessEvent(event) => sender() ! readDao.processEvent(event)
  }
}

object InMemoryReadActor {
  case class ProcessEvent(event: LogRecord)
  case object InitializeState
  case object GetGames

  val name = "in-memory-read-actor"
  val path = s"/user/$name"
  def props(logRecords: Seq[LogRecord]) =
    Props(new InMemoryReadActor(logRecords))
}

