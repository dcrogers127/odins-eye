package events.gameInfo

import java.util.UUID

import events.EventData
import model.GameBase
import play.api.libs.json.{JsValue, Json, Reads}

case class GameCreated(id: UUID, game: GameBase) extends EventData {
  override def action: String = GameCreated.actionName
  override def json: JsValue = Json.writes[GameCreated].writes(this)
}

object GameCreated {
  val actionName = "game-created"
  implicit val reads: Reads[GameCreated] = Json.reads[GameCreated]
}
