package events.gameInfo

import java.time.ZonedDateTime
import java.util.{Date, UUID}

import events.EventData
import play.api.libs.json.{JsValue, Json, Reads}

case class GameCreated(
    id: UUID,
    game_id: String,
    game_date: Date,
    start_time: ZonedDateTime,
    visitor: String,
    home: String
  ) extends EventData {
  override def action: String = GameCreated.actionName
  override def json: JsValue = Json.writes[GameCreated].writes(this)
}

object GameCreated {
  val actionName = "game-created"
  implicit val reads: Reads[GameCreated] = Json.reads[GameCreated]
}
