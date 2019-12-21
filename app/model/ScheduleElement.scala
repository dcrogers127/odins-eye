package model

import java.util.Date

import play.api.libs.json.Json

case class ScheduleElement (
  gameId: String,
  gameDate: Date,
  startEt: String,
  awayTeam: String,
  awayTm: String,
  awayPoints: Option[Int],
  homeTeam: String,
  homeTm: String,
  homePoints: Option[Int],
  overtime: String,
  attendance: String,
  notes: String,
  boxScoreUrl: String
)

object ScheduleElement {
  implicit val writes = Json.writes[ScheduleElement]
  implicit val reads = Json.reads[ScheduleElement]
}
