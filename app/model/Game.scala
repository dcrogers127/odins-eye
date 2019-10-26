package model

import scalikejdbc.WrappedResultSet

case class Game(
  game_date: String,
  visitor: String,
  visitor_pts: String,
  home: String,
  home_pts: String
)

object Game {
  def fromRS(rs: WrappedResultSet): Game = {
    Game(rs.string("game_date"),
      rs.string("visitor"), rs.string("visitor_pts"),
      rs.string("home"), rs.string("home_pts"))
  }
}
