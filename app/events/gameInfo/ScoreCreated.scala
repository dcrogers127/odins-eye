package events.gameInfo

import java.util.UUID

import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class ScoreCreated(
    id: UUID,
    game_id: String,
    visitor_pts: Int,
    home_pts: Int,
    ot: String
  ) extends EventData {
  override def action: String = ScoreCreated.actionName
  override def json: JsValue = Json.writes[ScoreCreated].writes(this)
}

object ScoreCreated {
  val actionName = "score-added"
  implicit val reads: Reads[ScoreCreated] = Json.reads[ScoreCreated]
}
