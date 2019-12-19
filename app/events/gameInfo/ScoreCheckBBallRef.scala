package events.gameInfo
import java.time.ZonedDateTime

import events.EventData
import model.GameId
import play.api.libs.json.{JsValue, Json, Reads}

case class ScoreCheckBBallRef(
                               timeScheduled: ZonedDateTime,
                               missingScores: Seq[GameId]
                             ) extends EventData {
  override def action: String = ScoreCheckBBallRef.actionName
  override def json: JsValue = Json.writes[ScoreCheckBBallRef].writes(this)
}

object ScoreCheckBBallRef {
  val actionName = "score-check-bball-ref"
  implicit val reads: Reads[ScoreCheckBBallRef] = Json.reads[ScoreCheckBBallRef]

}
