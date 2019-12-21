package model

import play.api.libs.json.Json

case class ScheduleElements(scheduleElements: Seq[ScheduleElement])

object ScheduleElements {
  implicit val writes = Json.writes[ScheduleElements]
  implicit val reads = Json.reads[ScheduleElements]
}
