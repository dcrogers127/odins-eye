package events.gameInfo

import java.time.ZonedDateTime
import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class ScalaUpdateSuccess (
                                timeScheduled: ZonedDateTime,
                                nSuccess: Int
                              ) extends EventData {
  override def action: String = ScalaUpdateSuccess.actionName
  override def json: JsValue = Json.writes[ScalaUpdateSuccess].writes(this)
}

object ScalaUpdateSuccess {
  val actionName = "score-update-failure"
  implicit val reads: Reads[ScalaUpdateSuccess] = Json.reads[ScalaUpdateSuccess]

}





