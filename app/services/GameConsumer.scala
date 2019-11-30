package services

import actors.{EventStreamActor, InMemoryReadActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import dao.GameDao
import model.{GameBase, LogRecord, ServerSentMessage}
import play.api.Configuration
import util.ServiceKafkaConsumer
import play.api.libs.json.Json

class GameConsumer(readService: ReadService, gameDao: GameDao, actorSystem: ActorSystem,
                       configuration: Configuration, materializer: Materializer) {

  val topicName = "games"
  val serviceKafkaConsumer = new ServiceKafkaConsumer(Set(topicName),
    "read", materializer, actorSystem, configuration, handleEvent)

  private def handleEvent(event: String): Unit = {
    val maybeLogRecord = LogRecord.decode(event)
    maybeLogRecord.foreach { logRecord =>
      adjustReadState(logRecord)
      val maybeGameBase = Json.fromJson[GameBase](logRecord.data)
      // maybeGameBase.foreach{ gameBase =>
      //   gameDao.insertGame(gameBase)
      // }
    }
  }

  import java.util.concurrent.TimeUnit
  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.ExecutionContext.Implicits.global

  private def adjustReadState(logRecord: LogRecord): Unit = {
    implicit val timeout = Timeout.apply(5, TimeUnit.SECONDS)
    val imrActor = actorSystem.actorSelection(InMemoryReadActor.path)
    (imrActor ? InMemoryReadActor.ProcessEvent(logRecord)).foreach { _ =>
      readService.getAllGames.foreach { games =>
        val update = ServerSentMessage.create("games", games)
        val esActor = actorSystem.actorSelection(EventStreamActor.pathPattern)
        esActor ! EventStreamActor.DataUpdated(update.json)
      }
    }
  }
}
