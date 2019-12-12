package services

import java.util.Date

import akka.actor.ActorSystem
import akka.stream.Materializer
import util.Common.monthNumToStr
import dao.{BBallRefDao, GameDao}
import events.gameInfo.ScoreCheck
import play.api.Configuration
import util.ServiceKafkaConsumer
import model.{GameId, LogRecord, ScheduleElement}

class ScoreCheckConsumer(actorSystem: ActorSystem, configuration: Configuration, materializer: Materializer,
                         gameDao: GameDao, bBallRefDao: BBallRefDao) {

  val topicName = "games"
  val serviceKafkaConsumer = new ServiceKafkaConsumer(Set(topicName),
    "scheduler", materializer, actorSystem, configuration, handleEvent)

  private def handleEvent(event: String): Unit = {
    val maybeLogRecord = LogRecord.decode(event)
    maybeLogRecord.foreach{
      _.action match {
        case ScoreCheck.actionName => checkUpdateScores(_)
        case _ => Unit
      }
    }
  }

  private def checkUpdateScores(logRecord: LogRecord): Unit = {
    val scoreCheck = logRecord.data.as[ScoreCheck]
    val today = java.sql.Date.valueOf(scoreCheck.timeScheduled.toLocalDate)
    val newScores = checkScores(today)
    updateScores(newScores)
  }

  private def checkScores(today: Date): Seq[ScheduleElement] = {
    val gameIdsNoScore = gameDao.missingScores(today)
    val gameIdsNoScoreStr = gameDao.missingScores(today).map(_.gameId)
    val monthsToCheck = missingScoreMonths(gameIdsNoScore)

    val newGameIds = monthsToCheck.flatMap{ case (year, month) =>
      bBallRefDao.extractScheduleByMonth(year, month)
    }.filter(game =>
      game.homePoints.isDefined && gameIdsNoScoreStr.contains(game.gameId)
    )

    newGameIds
  }

  private def updateScores(scores: Seq[ScheduleElement]): Unit =
    scores.foreach{ gameDao.updateScore }

  private def missingScoreMonths(gameIds: Seq[GameId]): Seq[(String, String)] = {
    gameIds.map{gameId =>
      val gameIdParts = gameId.gameId.split("-")
      val yearPart = gameIdParts(0)
      val monthPart = gameIdParts(1).toInt
      val year = if (monthPart < 8) yearPart else (yearPart.toInt + 1).toString
      val month = monthNumToStr(monthPart)
      (year, month)
    }.distinct
  }

}
