package services

import actors.InMemoryReadActor
import akka.actor.ActorSystem
import dao.{GameDao, LogDao}
import model.Game
import play.api.Logger

import scala.util.{Failure, Success}

class ReadService(actorSystem: ActorSystem, gameDao: GameDao, gameProducer: GameProducer) {
  val log = Logger(this.getClass)

  def init(initDB: Boolean = false): Unit = {
    if (initDB) {
      gameDao.cleanGameDB
      gameDao.initGameDB
    }
    val gamesT = gameDao.initGames
    gamesT match {
      case Failure(th) =>
        log.error("Error while initializing the game read service", th)
        throw th
      case Success(games) =>
        val actor = actorSystem.actorOf(
          InMemoryReadActor.props(games), InMemoryReadActor.name)
        actor ! InMemoryReadActor.InitializeState
    }
  }

  import java.util.concurrent.TimeUnit
  import akka.util.Timeout
  import scala.concurrent.Future
  import akka.pattern.ask

  def getGames: Future[Seq[Game]] = {
    implicit val timeout = Timeout.apply(5, TimeUnit.SECONDS)
    val actor = actorSystem.actorSelection(InMemoryReadActor.path)
    (actor ? InMemoryReadActor.GetGames).mapTo[Seq[Game]]
  }
}

