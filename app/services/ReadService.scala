package services

import actors.InMemoryReadActor
import akka.actor.ActorSystem

import dao.LogDao
import model.Game
import play.api.Logger

import scala.util.{Failure, Success}

class ReadService(actorSystem: ActorSystem, logDao: LogDao) {
  val log = Logger(this.getClass)

  def init(): Unit = {
    val logRecordsT = logDao.getLogRecords
    logRecordsT match {
      case Failure(th) =>
        log.error("Error while initializing the read service", th)
        throw th
      case Success(logRecords) =>
        val actor = actorSystem.actorOf(
          InMemoryReadActor.props(logRecords), InMemoryReadActor.name)
        actor ! InMemoryReadActor.InitializeState
    }
  }

  import java.util.concurrent.TimeUnit
  import akka.util.Timeout
  import scala.concurrent.Future
  import akka.pattern.ask

  def getGames: Future[Seq[Game]] = {
    implicit val timeout = Timeout.apply(5, TimeUnit.SECONDS)
    val actor = actorSystem.actorSelection(InMemoryReadActor.path)
    (actor ? InMemoryReadActor.GetTags).mapTo[Seq[Game]]
  }
}

