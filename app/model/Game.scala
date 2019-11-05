package model

import scalikejdbc.WrappedResultSet
import play.api.libs.json.Json

case class Game(
  id: Int,
  game_date: String,
  visitor: String,
  visitor_pts: String,
  home: String,
  home_pts: String
)

object Game {
  def fromRS(rs: WrappedResultSet): Game = {
    Game(rs.int("id"), rs.string("game_date"),
      rs.string("visitor"), rs.string("visitor_pts"),
      rs.string("home"), rs.string("home_pts"))
  }

  implicit val writes = Json.writes[Game]
}


