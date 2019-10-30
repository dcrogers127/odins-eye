package controllers

import model.WebPageData
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import security.UserAuthAction
import services.ReadService

class GameController(components: ControllerComponents,
                     userAuthAction: UserAuthAction, readService: ReadService) extends AbstractController(components) {


  import scala.concurrent.ExecutionContext.Implicits.global
  def getGames = Action.async { implicit request =>
    val gamesF = readService.getGames
    gamesF.map { games => Ok(Json.toJson(games)) }
  }

  def index = Action { request =>
    Ok(views.html.pages.react(buildNavData(request), WebPageData("Games")))
  }
}
