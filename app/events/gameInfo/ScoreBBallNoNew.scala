package events.gameInfo

import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}
import java.time.ZonedDateTime

case class ScoreBBallNoNew (
  timeScheduled: ZonedDateTime
) extends EventData {
  import play.api.libs.json.Json
  override def action: String = ScoreBBallNoNew.actionName
  override def json: JsValue = Json.writes[ScoreBBallNoNew].writes(this)
}

object ScoreBBallNoNew {
  val actionName = "score-bball-no-new"
  implicit val reads: Reads[ScoreBBallNoNew] = Json.reads[ScoreBBallNoNew]

}


