package services

import actors.{EventStreamActor, InMemoryReadActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import model.LogRecord
import model.ServerSentMessage
import play.api.Configuration
import util.ServiceKafkaConsumer

class GameConsumer(readService: ReadService, actorSystem: ActorSystem,
                       configuration: Configuration, materializer: Materializer) {

  val topicName = "games"
  val serviceKafkaConsumer = new ServiceKafkaConsumer(Set(topicName),
    "read", materializer, actorSystem, configuration, handleEvent)

  private def handleEvent(event: String): Unit = {
    val maybeLogRecord = LogRecord.decode(event)
    maybeLogRecord.foreach(adjustReadState)
  }

  import java.util.concurrent.TimeUnit
  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.ExecutionContext.Implicits.global

  private def adjustReadState(logRecord: LogRecord): Unit = {
    implicit val timeout = Timeout.apply(5, TimeUnit.SECONDS)
    val imrActor = actorSystem.actorSelection(InMemoryReadActor.path)
    (imrActor ? InMemoryReadActor.ProcessEvent(logRecord)).foreach { _ =>
      readService.getGames.foreach { games =>
        val update = ServerSentMessage.create("games", games)
        val esActor = actorSystem.actorSelection(EventStreamActor.pathPattern)
        esActor ! EventStreamActor.DataUpdated(update.json)
      }
    }
  }
}
