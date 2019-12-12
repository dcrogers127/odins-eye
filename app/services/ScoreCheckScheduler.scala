package services

import java.time.ZonedDateTime
import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import events.EventData
import events.gameInfo.ScoreCheck
import model.LogRecord
import play.api.Configuration
import util.ServiceKafkaProducer

import scala.concurrent.duration._

class ScoreCheckScheduler(actorSystem: ActorSystem, configuration: Configuration)(
  implicit val materializer: Materializer
) {

  val topicName = "score-check"
  val kafkaProducer = new ServiceKafkaProducer(topicName, actorSystem, configuration)

  def init(): Unit =
    Source.tick(
        5.seconds,
        60.seconds,
        "tick"
      ).map(_ => createLogRecord(ScoreCheck(ZonedDateTime.now())).encode)
      .map(kafkaProducer.send)
      .runWith(Sink.ignore)


  private def createLogRecord(eventData: EventData): LogRecord =
    LogRecord(UUID.randomUUID(), eventData.action, eventData.json, ZonedDateTime.now())
}
