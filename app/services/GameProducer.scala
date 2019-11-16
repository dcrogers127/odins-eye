package services

import akka.actor.ActorSystem
import java.time.ZonedDateTime
import java.util.UUID

import events.EventData
import events.gameInfo.GameCreated
import model.{GameBase, LogRecord, Score}
import play.api.Configuration
import util.ServiceKafkaProducer

class GameProducer(actorSystem: ActorSystem, configuration: Configuration) {

  val kafkaProducer = new ServiceKafkaProducer("games", actorSystem, configuration)

  def createGame(game: GameBase): Unit = {
    val id = UUID.randomUUID()
    val event = GameCreated(
      id,
      game.game_id,
      game.game_date,
      game.start_time,
      game.visitor,
      game.home
    )
    val record = createLogRecord(event)
    kafkaProducer.send(record.encode)
  }

  // def addScore(game: Score): Unit = {
  //   val id = UUID.randomUUID()
  //   val event = GameCreated(id, game)
  //   val record = createLogRecord(event)
  //   kafkaProducer.send(record.encode)
  // }

  private def createLogRecord(eventData: EventData): LogRecord = {
    LogRecord(UUID.randomUUID(), eventData.action, eventData.json, ZonedDateTime.now())
  }

}
