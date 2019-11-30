package controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import security.UserAuthAction
import services.ReadService

class GameController(components: ControllerComponents,
                     userAuthAction: UserAuthAction, readService: ReadService) extends AbstractController(components) {

  import scala.concurrent.ExecutionContext.Implicits.global
  def getGames = userAuthAction.async { implicit request =>
    // delete me
    println(request.getQueryString("startDate"))
    println(request.getQueryString("endDate"))

    val gamesF = readService.getGames(
      request.getQueryString("startDate"),
      request.getQueryString("endDate")
    )
    gamesF.map { games => Ok(Json.toJson(games)) }
  }
}
