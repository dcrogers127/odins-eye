package model

import scalikejdbc.WrappedResultSet
import play.api.libs.json.Json

import scala.util.Try

case class Game(
   gameId: String,
   gameDate: String,
   startEt: String,
   awayTeam: String,
   awayTm: String,
   homeTeam: String,
   homeTm: String,
   awayPoints: String,
   homePoints: String,
   overtime: String
 )

object Game {
  def fromRS(rs: WrappedResultSet): Game = {
    Game(
      rs.string("game_id"),
      rs.string("game_date"),
      rs.string("start_time"),
      rs.string("away_team"),
      rs.string("away_tm"),
      rs.string("home_team"),
      rs.string("home_tm"),
      Try(rs.int("away_pts").toString).toOption.getOrElse(""),
      Try(rs.int("home_pts").toString).toOption.getOrElse(""),
      rs.string("ot")
    )
  }

  implicit val writes = Json.writes[Game]
}


