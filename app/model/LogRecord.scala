package model

import java.util.UUID
import java.time.ZonedDateTime
import play.api.libs.json.JsValue

case class LogRecord(id: UUID, action: String, data: JsValue, timestamp: ZonedDateTime)

