package model

import java.util.Date

case class ScheduleElement (
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
