package events.gameInfo

import java.util.UUID

import events.EventData
import model.Score
import play.api.libs.json.{JsValue, Json, Reads}

case class ScoreCreated(id: UUID, games: Score) extends EventData {
  override def action: String = ScoreCreated.actionName
  override def json: JsValue = Json.writes[ScoreCreated].writes(this)
}

object ScoreCreated {
  val actionName = "score-added"
  implicit val reads: Reads[ScoreCreated] = Json.reads[ScoreCreated]
}
