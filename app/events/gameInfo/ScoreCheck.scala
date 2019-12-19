package events.gameInfo

import java.time.ZonedDateTime

import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class ScoreCheck(
  timeScheduled: ZonedDateTime
) extends EventData {
  override def action: String = ScoreCheck.actionName
  override def json: JsValue = Json.writes[ScoreCheck].writes(this)
}

object ScoreCheck {
  val actionName = "score-check"
  implicit val reads: Reads[ScoreCheck] = Json.reads[ScoreCheck]
}
