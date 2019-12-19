package services

import akka.actor.ActorSystem
import akka.stream.Materializer
import util.Common.monthNumToStr
import dao.{BBallRefDao, GameDao}
import events.gameInfo.{ScalaUpdateFailure, ScoreCheckBBallRef, ScoreUpdateDB, _}
import play.api.Configuration
import util.{ServiceKafkaConsumer, ServiceKafkaProducer}
import model._

import scala.util.{Failure, Success, Try}

class ScoreConsumer(actorSystem: ActorSystem, configuration: Configuration, materializer: Materializer,
                         gameDao: GameDao, bBallRefDao: BBallRefDao) {

  val topicName = "games"
  val kafkaProducer = new ServiceKafkaProducer(topicName, actorSystem, configuration)
  val serviceKafkaConsumer = new ServiceKafkaConsumer(Set(topicName),
    "scheduler", materializer, actorSystem, configuration, handleEvent)

  private def handleEvent(event: String): Unit = {
    val maybeLogRecord = LogRecord.decode(event)
    maybeLogRecord.foreach{
      _.action match {
        case ScoreCheck.actionName => checkScoreGameDao(_)
        case ScoreCheckBBallRef.actionName => checkScoresBBallRef(_)
        case ScoreUpdateDB.actionName => updateScores(_)
        case ScoreUpToDate.actionName => Unit   // No action needed
        case ScoreBBallNoNew.actionName => Unit // No action needed
        case ScalaUpdateSuccess.actionName => Unit // No action needed
        case ScalaUpdateFailure.actionName => Unit // No action needed
        case _ => Unit // Unknown action
      }
    }
  }

  private def checkScoreGameDao(logRecord: LogRecord): Unit = {
    val uuid = logRecord.id
    val scoreCheck = logRecord.data.as[ScoreCheck]
    val timerScheduled = scoreCheck.timeScheduled
    val today = java.sql.Date.valueOf(scoreCheck.timeScheduled.toLocalDate)

    val missingScores = gameDao.missingScores(today)
    val event = missingScores match {
      case Seq() => LogRecord.createLogRecord(uuid, ScoreUpToDate(timerScheduled, today)).encode
      case s: Seq[GameId] =>
        val scoreCheckBBallRef = ScoreCheckBBallRef(timerScheduled, s)
        LogRecord.createLogRecord(uuid, scoreCheckBBallRef).encode
    }
    kafkaProducer.send(event)
  }

  private def checkScoresBBallRef(logRecord: LogRecord): Unit = {
    val uuid = logRecord.id
    val scoreCheckBBallRef = logRecord.data.as[ScoreCheckBBallRef]
    val timerScheduled = scoreCheckBBallRef.timeScheduled
    val gameIdsNoScore = scoreCheckBBallRef.missingScores

    val gameIdsNoScoreStr = gameIdsNoScore.map(_.gameId)
    val monthsToCheck = missingScoreMonths(gameIdsNoScore)

    val newGameIds = monthsToCheck.flatMap{ case (year, month) =>
      bBallRefDao.extractScheduleByMonth(year, month)
    }.filter(game =>
      game.homePoints.isDefined && gameIdsNoScoreStr.contains(game.gameId)
    )

    val event = newGameIds match {
      case Seq() => LogRecord.createLogRecord(uuid, ScoreBBallNoNew(timerScheduled)).encode
      case s: Seq[ScheduleElement] =>
        val scoreUpdateDB = ScoreUpdateDB(timerScheduled, newGameIds)
        LogRecord.createLogRecord(uuid, scoreUpdateDB).encode
    }
    kafkaProducer.send(event)
  }

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

  private def updateScores(logRecord: LogRecord): Unit = {
    val uuid = logRecord.id
    val scoreUpdateDB = logRecord.data.as[ScoreUpdateDB]
    val timerScheduled = scoreUpdateDB.timeScheduled
    val scores =  scoreUpdateDB.newScores

    val failureSuccessCounts: (Int, Int) = scores.map(gameDao.updateScore)
      .foldLeft((0, 0): (Int, Int))((cnts, tryA) => tryA match {
        case Success(_) => (cnts._1 + 1, cnts._2)
        case Failure(_) => (cnts._1, cnts._2 + 1)
      })

    val event = if (failureSuccessCounts._2 == 0) {
      val scalaUpdateSuccess = ScalaUpdateSuccess(timerScheduled, failureSuccessCounts._1)
      LogRecord.createLogRecord(uuid, scalaUpdateSuccess).encode
    } else {
      val scalaUpdateFailure = ScalaUpdateFailure(timerScheduled, failureSuccessCounts._1, failureSuccessCounts._2)
      LogRecord.createLogRecord(uuid, scalaUpdateFailure).encode
    }
    kafkaProducer.send(event)
  }
}
