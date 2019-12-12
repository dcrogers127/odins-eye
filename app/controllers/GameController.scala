package controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import security.UserAuthAction
import services.ReadService

class GameController(components: ControllerComponents,
                     userAuthAction: UserAuthAction, readService: ReadService) extends AbstractController(components) {

  import scala.concurrent.ExecutionContext.Implicits.global
  def getGames = userAuthAction { implicit request =>
    val games = readService.getGames(
      request.getQueryString("startDate"),
      request.getQueryString("endDate")
    )
    Ok(Json.toJson(games))
  }
}
