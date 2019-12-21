package model

import play.api.libs.json.Json

case class GameIds(gameIds: Seq[GameId])

object GameIds {
  implicit val writes = Json.writes[GameIds]
  implicit val reads = Json.reads[GameIds]
}

