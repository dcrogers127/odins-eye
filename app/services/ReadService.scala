package services

import java.util.Date

import util.Common.{dateFormat, currentYear}
import dao.{BBallRefDao, GameDao}
import model.Game

import scala.util.{Success, Try}

class ReadService(gameDao: GameDao, bBallRefDao: BBallRefDao) {
  def init(initDB: Boolean = true): Unit = {
    if (initDB) {
      gameDao.cleanGameDB
      val games = bBallRefDao.getSchedule(currentYear)
      gameDao.initGameDB(games)
    }
  }

  def getGames(maybeStartDate: Option[String], maybeEndDate: Option[String]): Seq[Game] = {
    val mStartDate = convertToDate(maybeStartDate)
    val mEndDate = convertToDate(maybeEndDate)
    (mStartDate, mEndDate) match {
      case (Some(startDate), Some(endDate)) =>
        if (startDate.after(endDate)) Seq()
        else {
          val tryGames = gameDao.getGamesFromDb(startDate, endDate)
          tryGames match {
            case Success(games) => games.sortWith(_.gameId < _.gameId)
            // ToDo: Throw failure or something here
            case _ => Seq()
          }
        }
      case _ => Seq()
    }
  }

  def getAllGames: Seq[Game] = {
    val tryAllGames = gameDao.getAllGamesFromDb
    tryAllGames match {
      case Success(games) =>games.sortWith(_.gameId < _.gameId)
      // ToDo: Throw failure or something here
      case _ => Seq()
    }
  }

  def convertToDate(maybeDate: Option[String]): Option[Date] =
    maybeDate.flatMap(x => Try(dateFormat.parse(x)).toOption)
}

