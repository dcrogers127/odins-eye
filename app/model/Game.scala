package model

import java.time.ZonedDateTime
import java.util.Date

import scalikejdbc.WrappedResultSet
import play.api.libs.json.Json

case class Game(
  game_id: String,
  game_date: Date,
  start_time: String,
  visitor: String,
  visitor_pts: String,
  home: String,
  home_pts: String
)

object Game {
  def fromRS(rs: WrappedResultSet): Game = {
    Game(rs.string("game_id"),
      rs.date("game_date"), rs.string("start_time"),
      rs.string("visitor"), rs.string("visitor_pts"),
      rs.string("home"), rs.string("home_pts"))
  }

  implicit val writes = Json.writes[Game]
}


