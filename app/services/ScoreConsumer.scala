package services

import akka.actor.ActorSystem
import akka.stream.Materializer
import util.Common.monthNumToStr
import dao.{BBallRefDao, GameDao}
import events.gameInfo._
import play.api.Configuration
import util.{ServiceKafkaConsumer, ServiceKafkaProducer}
import model._

import scala.util.{Failure, Success}

class ScoreConsumer(actorSystem: ActorSystem, configuration: Configuration, materializer: Materializer,
                         gameDao: GameDao, bBallRefDao: BBallRefDao) {

  val scoreTopicName = "score"
  val logTopicName = "log"
  val kafkaProducer = new ServiceKafkaProducer(actorSystem, configuration)
  val serviceKafkaConsumer = new ServiceKafkaConsumer(Set(scoreTopicName),
    "scheduler", materializer, actorSystem, configuration, handleEvent)

  private def handleEvent(event: String): Unit = {
    val maybeLogRecord = LogRecord.decode(event)
    maybeLogRecord.foreach{ logRecord =>
      logRecord.action match {
        case ScoreCheck.actionName => checkScoreGameDao(logRecord)
        case ScoreCheckBBallRef.actionName => checkScoresBBallRef(logRecord)
        case ScoreUpdateDB.actionName => updateScores(logRecord)
        case ScalaUpdateSuccess.actionName => Unit // Calc stats
        case ScoreUpToDate.actionName => Unit   // No action needed
        case ScoreBBallNoNew.actionName => Unit // No action needed
        case ScalaUpdateFailure.actionName => Unit // No action needed
        case _ => Unit // Unknown action
      }
    }
  }

  private def checkScoreGameDao(logRecord: LogRecord): Unit = {
    val uuid = logRecord.orig_record_id
    val scoreCheck = logRecord.data.as[ScoreCheck]
    val timerScheduled = scoreCheck.timeScheduled
    val today = java.sql.Date.valueOf(scoreCheck.timeScheduled.toLocalDate)

    val missingScores = gameDao.missingScores(today)
    val event = missingScores match {
      case Seq() => LogRecord.createLogRecord(uuid, ScoreUpToDate(timerScheduled, today)).encode
      case s: Seq[GameId] =>
        val scoreCheckBBallRef = ScoreCheckBBallRef(timerScheduled, GameIds(s))
        LogRecord.createLogRecord(uuid, scoreCheckBBallRef).encode
    }
    kafkaProducer.send(event, scoreTopicName)
  }

  private def checkScoresBBallRef(logRecord: LogRecord): Unit = {
    val uuid = logRecord.orig_record_id
    val scoreCheckBBallRef = logRecord.data.as[ScoreCheckBBallRef]
    val timerScheduled = scoreCheckBBallRef.timeScheduled
    val gameIdsNoScore = scoreCheckBBallRef.missingScores

    val gameIdsNoScoreStr = gameIdsNoScore.gameIds.map(_.gameId)
    val monthsToCheck = missingScoreMonths(gameIdsNoScore.gameIds)

    val newGameIds = monthsToCheck.flatMap{ case (year, month) =>
      bBallRefDao.extractScheduleByMonth(year, month)
    }.filter(game =>
      game.homePoints.isDefined && gameIdsNoScoreStr.contains(game.gameId)
    )

    val (event, topicName) = newGameIds match {
      case Seq() => (LogRecord.createLogRecord(uuid, ScoreBBallNoNew(timerScheduled)).encode, logTopicName)
      case s: Seq[ScheduleElement] =>
        val scoreUpdateDB = ScoreUpdateDB(timerScheduled, ScheduleElements(s))
        (LogRecord.createLogRecord(uuid, scoreUpdateDB).encode, scoreTopicName)
    }
    kafkaProducer.send(event, topicName)
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
    val uuid = logRecord.orig_record_id
    val scoreUpdateDB = logRecord.data.as[ScoreUpdateDB]
    val timerScheduled = scoreUpdateDB.timeScheduled
    val scores =  scoreUpdateDB.newScores

    val failureSuccessCounts: (Int, Int) = scores.scheduleElements.map(gameDao.updateScore)
      .foldLeft((0, 0): (Int, Int))((counts, tryA) => tryA match {
        case Success(_) => (counts._1 + 1, counts._2)
        case Failure(_) => (counts._1, counts._2 + 1)
      })

    val (event, topicName) = if (failureSuccessCounts._2 == 0) {
      val scalaUpdateSuccess = ScalaUpdateSuccess(timerScheduled, failureSuccessCounts._1)
      (LogRecord.createLogRecord(uuid, scalaUpdateSuccess).encode, logTopicName)
    } else {
      val scalaUpdateFailure = ScalaUpdateFailure(timerScheduled, failureSuccessCounts._1, failureSuccessCounts._2)
      (LogRecord.createLogRecord(uuid, scalaUpdateFailure).encode, logTopicName)
    }
    kafkaProducer.send(event, topicName)
  }
}
