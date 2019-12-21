package model

import play.api.libs.json.Json
import scalikejdbc.WrappedResultSet

case class GameId (gameId: String)

object GameId {
  def fromRS(rs: WrappedResultSet): GameId = {
    GameId(rs.string("game_id"))
  }

  implicit val writes = Json.writes[GameId]
  implicit val reads = Json.reads[GameId]
}
