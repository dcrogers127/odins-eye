package dao

import model.Game
import scalikejdbc._

import scala.util.Try

class GameDao {

  def getGames: Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"select * from games".map(Game.fromRS).list().apply()
    }
  }

}
