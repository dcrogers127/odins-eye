package services

import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import events.gameInfo.ScoreCheck
import model.LogRecord
import play.api.Configuration
import util.ServiceKafkaProducer

import scala.concurrent.duration._

class ScoreCheckScheduler(actorSystem: ActorSystem, configuration: Configuration)(
  implicit val materializer: Materializer
) {

  val topicName = "score"
  val kafkaProducer = new ServiceKafkaProducer(actorSystem, configuration)

  def init(): Unit =
    Source.tick(
        30.seconds,
        30.seconds,
        "tick"
      ).map{ _ =>
        val event = LogRecord.createLogRecord(ScoreCheck(ZonedDateTime.now())).encode
        kafkaProducer.send(event, topicName)
      }
      .runWith(Sink.ignore)

}
