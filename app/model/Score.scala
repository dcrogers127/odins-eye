package model
import play.api.libs.json.Json

case class Score (
  game_id: String,
  visitor_pts: Int,
  home_pts: Int,
  ot: String
)

object Score {
  implicit val writes = Json.writes[Game]
}
