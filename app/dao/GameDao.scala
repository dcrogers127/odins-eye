package dao

import model.{Game, GameCSV}
import fileio.RowParser.rowParserFor
import scalikejdbc._
import au.com.bytecode.opencsv._

import scala.collection.JavaConverters._
import scala.util.{Success, Try}

class GameDao {
  def insertGames: Unit = {
    val dataPath = "public/data/games.csv"
    val reader = new CSVReader(new java.io.FileReader(dataPath))
    val rows = reader.readAll.asScala.map(row => rowParserFor[GameCSV](row.toList))
    NamedDB('statsstore).localTx{ implicit session =>
      sql"delete from games".update().apply()
      for (row <- rows) {
        row match {
          case Success(GameCSV(
            game_date,
            start_et,
            visitor,
            visitor_pts,
            home,
            home_pts,
            box_score_url,
            ot,
            attend,
            notes
          )) => sql"""insert into games (
                   game_date,
                   start_et,
                   visitor,
                   visitor_pts,
                   home,
                   home_pts,
                   box_score_url,
                   ot,
                   attend,
                   notes
                 ) values (
                   $game_date,
                   $start_et,
                   $visitor,
                   $visitor_pts,
                   $home,
                   $home_pts,
                   $box_score_url,
                   $ot,
                   $attend,
                   $notes
                 )""".update().apply()
          case _ => Unit
        }
      }
    }
  }

  def initGames: Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"select * from games".map(Game.fromRS).list().apply()
    }
  }
}
