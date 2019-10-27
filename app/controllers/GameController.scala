package controllers
import play.api.mvc.{AbstractController, ControllerComponents}
import security.UserAuthAction

class GameController(components: ControllerComponents,
                     userAuthAction: UserAuthAction, readService: ReadService) extends AbstractController(components) {

}
