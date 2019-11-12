package model

import java.util.Date
import java.time.ZonedDateTime

case class GameBase (
  game_id: String,
  game_date: Date,
  start_time: ZonedDateTime,
  visitor: String,
  home: String
)
