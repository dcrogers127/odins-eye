package events.gameInfo
import java.time.ZonedDateTime

import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class ClearStatsDB (

                       timeScheduled: ZonedDateTime
                     ) extends EventData {
  override def action: String = ClearStatsDB.actionName
  override def json: JsValue = Json.writes[ClearStatsDB].writes(this)
}

object ClearStatsDB {
  val actionName = "clear-stats-db"
  implicit val reads: Reads[ClearStatsDB] = Json.reads[ClearStatsDB]
}

