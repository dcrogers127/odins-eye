package events.gameInfo
import java.time.ZonedDateTime
import java.util.Date

import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class ScoreUpToDate(
    timeScheduled: ZonedDateTime,
    today: Date
  ) extends EventData {
    override def action: String = ScoreUpToDate.actionName
    override def json: JsValue = Json.writes[ScoreUpToDate].writes(this)

}

object ScoreUpToDate {
  val actionName = "score-up-to-date"
  implicit val reads: Reads[ScoreUpToDate] = Json.reads[ScoreUpToDate]
}
