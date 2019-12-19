package events.gameInfo

import java.time.ZonedDateTime
import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class ScalaUpdateFailure (
                                timeScheduled: ZonedDateTime,
                                nSuccess: Int,
                                nFailure: Int
                              ) extends EventData {
  override def action: String = ScalaUpdateFailure.actionName
  override def json: JsValue = Json.writes[ScalaUpdateFailure].writes(this)
}

object ScalaUpdateFailure {
  val actionName = "score-update-failure"
  implicit val reads: Reads[ScalaUpdateFailure] = Json.reads[ScalaUpdateFailure]

}



