package model

import java.util.UUID
import java.time.ZonedDateTime

import events.EventData
import play.api.libs.json.{JsValue, Json}

case class LogRecord(id: UUID, action: String, data: JsValue, timestamp: ZonedDateTime) {
  def encode: String = {
    Json.toJson(this)(LogRecord.writes).toString()
  }
}

object LogRecord {
  val writes = Json.writes[LogRecord]
  val reads = Json.reads[LogRecord]

  def decode(str: String): Option[LogRecord] = {
    Json.parse(str).asOpt[LogRecord](reads)
  }

  def createLogRecord(eventData: EventData): LogRecord =
    LogRecord(UUID.randomUUID(), eventData.action, eventData.json, ZonedDateTime.now())

  def createLogRecord(uuid: UUID, eventData: EventData): LogRecord =
    LogRecord(uuid, eventData.action, eventData.json, ZonedDateTime.now())
}
