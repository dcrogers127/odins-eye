package dao

import java.util.Date

import model._
import scalikejdbc._
import play.api.Logger

import scala.util.Try
import scalikejdbc.TxBoundary.Try._

class GameDao {
  val log = Logger(this.getClass)

  def getAllGamesFromDb: Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"""
        select
          game_id,
          game_date,
          start_time,
          away_team,
          away_tm,
          home_team,
          home_tm,
          away_pts,
          home_pts,
          ot
        from games""".map(Game.fromRS).list().apply()
    }
  }

  def getGamesFromDb(startDate: Date, endDate: Date): Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"""
        select
          game_id,
          game_date,
          start_time,
          away_team,
          away_tm,
          home_team,
          home_tm,
          away_pts,
          home_pts,
          ot
        from games
        where game_date between $startDate and $endDate
        """.map(Game.fromRS).list().apply()
    }
  }

  def cleanGameDB: Unit = {
    NamedDB('statsstore).localTx { implicit session =>
      sql"delete from games".update().apply()
    }
  }

  def initGameDB(games: Seq[ScheduleElement]): Unit = {
    NamedDB('statsstore).localTx{ implicit session =>
      for (game <- games) {
        sql"""insert into games (
          game_id,
          game_date,
          start_time,
          away_team,
          away_tm,
          home_team,
          home_tm,
          away_pts,
          home_pts,
          ot,
          notes,
          box_score_url
        ) values (
          ${game.gameId},
          ${game.gameDate},
          ${game.startEt},
          ${game.awayTeam},
          ${game.awayTm},
          ${game.homeTeam},
          ${game.homeTm},
          ${game.awayPoints},
          ${game.homePoints},
          ${game.overtime},
          ${game.notes},
          ${game.boxScoreUrl}
        )""".update().apply()
      }
    }
  }

  def updateScore(score: ScheduleElement): Try[Unit] = {
    NamedDB('statsstore).localTx{ implicit session =>
      Try {
        sql"""update games
          set away_pts = ${score.awayPoints.get},
             home_pts = ${score.homePoints.get},
             ot = ${score.overtime}
          where game_id = ${score.gameId}
         """.update().apply()
      }
    }
  }

  def missingScores(endDate: Date): Seq[GameId] = {
    NamedDB('statsstore).localTx{ implicit session =>
      sql"""
        select distinct game_id
        from games
        where home_pts is NULL
          and game_date < $endDate
      """.map(GameId.fromRS).list().apply()
    }
  }

}
