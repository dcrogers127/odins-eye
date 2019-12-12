package model

import scalikejdbc.WrappedResultSet

case class GameId (gameId: String)

object GameId {
  def fromRS(rs: WrappedResultSet): GameId = {
    GameId(rs.string("game_id"))
  }
}
