package dao

import model.Game
import scalikejdbc._
import scala.io.Source.fromFile

import scala.util.Try

class GameDao {
  def insertGames: Unit = {
    val dataPath = "public/data/games.csv"
    val bufSource = fromFile(dataPath)
    for (line <- bufSource.getLines) {
      line.split(",").map(_.trim)
      // insert to games
    }
  }


  def getGames: Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"select * from games".map(Game.fromRS).list().apply()
    }
  }

}
