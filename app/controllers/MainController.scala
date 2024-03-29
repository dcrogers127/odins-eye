package controllers

import controllers.Assets.Asset
import model._
import play.api.libs.json.JsValue
import play.api.mvc._
import security.{UserAuthAction, UserAwareAction, UserAwareRequest}
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import services.ConsumerAggregator

class MainController(components: ControllerComponents, assets: Assets,
                     actorSystem: ActorSystem,
                     mat: Materializer,
                     consumerAggregator: ConsumerAggregator,
                     userAuthAction: UserAuthAction, userAwareAction: UserAwareAction)
  extends AbstractController(components) {

  def index = userAwareAction { request =>
    Ok(views.html.pages.react(buildNavData(request), WebPageData("Home")))
  }

  def games = userAwareAction { request =>
    Ok(views.html.pages.games(buildNavData(request), WebPageData("Games")))
  }

  def error500 = Action {
    InternalServerError(views.html.errorPage())
  }

  private def buildNavData(request: UserAwareRequest[_]): NavigationData = {
    NavigationData(request.user, isLoggedIn = request.user.isDefined)
  }

  import actors.EventStreamActor
  import play.api.libs.EventSource
  import akka.stream.scaladsl._
  import play.api.http.ContentTypes
  def serverEventStream = userAwareAction { request =>
    implicit val materializer = mat
    implicit val actorFactory = actorSystem
    val maybeUser = request.user
    val maybeUserId = maybeUser.map(_.userId)
    val (out, publisher) = Source.actorRef[JsValue](
      bufferSize = 16, OverflowStrategy.dropNew)
      .toMat(Sink.asPublisher(fanout = false))(Keep.both).run()
    actorSystem.actorOf(EventStreamActor.props(out),
      EventStreamActor.name(maybeUserId))
    val source = Source.fromPublisher(publisher)
    Ok.chunked(source.via(EventSource.flow)).as(ContentTypes.EVENT_STREAM)
  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)
}
