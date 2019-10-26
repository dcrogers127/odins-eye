package dao

import model.Game
import scalikejdbc._

import scala.util.Try

class GameDao {

  def getUsers: Try[Seq[Game]] = Try {
    NamedDB('statsstore).readOnly { implicit session =>
      sql"select * from users".map(Game.fromRS).list().apply()
    }
  }

}
