package model

import java.util.UUID
import java.time.ZonedDateTime
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
}
