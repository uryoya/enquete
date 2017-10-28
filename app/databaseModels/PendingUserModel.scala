package databaseModels

import javax.inject._

import play.api.db._
import anorm._
import anorm.SqlParser._

case class PendingUserDbData(id: Int, name: String, accessToken: String)

@Singleton
class PendingUserModel(db: Database) {
  // pending user parser
  val pendingUserDataParser = int("id") ~ str("name") ~ str("access_token")
  val pendingUserDataMapper = pendingUserDataParser.map {
    case id~name~access_token => PendingUserDbData(id, name, access_token)
  }

  def addPendingUser(id: Int, name: String, accessToken: String): Option[Long] = {
    db.withConnection { implicit connect =>
      SQL("INSERT INTO pending_user (`id`, name, access_token) VALUES ({id}, {name}, {access_token});")
        .on("id" -> id, "name" -> name, "access_token" -> accessToken)
        .executeInsert()
    }
  }

  def getPendingUser(id: Int): PendingUserDbData = {
    db.withConnection{ implicit connect =>
      val data = SQL("SELECT * FROM pending_user WHERE `id` = {id};")
        .on("id" -> id).as(pendingUserDataMapper.*)
      if (data.length < 1)
        null
      else
        data(0)
    }
  }

  def getAll: List[PendingUserDbData] = {
    db.withConnection { implicit connect =>
      SQL("SELECT * FROM pending_user;")
        .as(pendingUserDataMapper.*)
    }
  }

  def delete(id: Int): Unit = {
    db.withConnection { implicit connect =>
      SQL("DELETE FROM pending_user WHERE id = {id};")
        .on("id" -> id)
        .executeUpdate()
    }
  }

  def authorize(id: Int): Unit = {
    val users: UserModel = new UserModel(db)
    val pendingUser = getPendingUser(id)
    users.addUser(pendingUser.id, pendingUser.name, new Array(0.toByte), pendingUser.accessToken, admin=false)
    delete(id)
  }
}
