package model

import java.util.Date
import java.time.ZonedDateTime

import play.api.libs.json.Json

case class GameBase (
  game_id: String,
  game_date: Date,
  start_time: ZonedDateTime,
  visitor: String,
  home: String
)

object GameBase {
  implicit val writes = Json.writes[Game]
}
