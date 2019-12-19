package events.gameInfo
import java.time.ZonedDateTime

import events.EventData
import model.ScheduleElement
import play.api.libs.json.{JsValue, Json, Reads}

case class ScoreUpdateDB (
                           timeScheduled: ZonedDateTime,
                           newScores: Seq[ScheduleElement]
                         ) extends EventData {
  override def action: String = ScoreUpdateDB.actionName
  override def json: JsValue = Json.writes[ScoreUpdateDB].writes(this)
}

object ScoreUpdateDB {
  val actionName = "score-update-db"
  implicit val reads: Reads[ScoreUpdateDB] = Json.reads[ScoreUpdateDB]

}

