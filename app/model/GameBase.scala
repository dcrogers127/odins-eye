package model

import java.util.Date

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet

case class GameBase (
  game_id: String,
  game_date: Date,
  start_time: String,
  visitor: String,
  home: String
)

object GameBase {
  def fromRS(rs: WrappedResultSet): GameBase = {
    GameBase(rs.string("game_id"),
      rs.date("game_date"), rs.string("start_time"),
      rs.string("visitor"), rs.string("home")
    )
  }

  implicit val writes = Json.writes[GameBase]
  implicit val reads = Json.reads[GameBase]
}
