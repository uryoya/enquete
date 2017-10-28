package databaseModels

import javax.inject._

import play.api.db._
import anorm._
import anorm.SqlParser._

case class UserDbData(id: Int, name: String, icon: Array[Byte], accessToken: String, admin: Int)

@Singleton
class UserModel(db: Database) {
    // user parser
    val userDataParser = int("id") ~ str("name") ~ byteArray("icon") ~ str("access_token") ~ int("admin")
    val userDataMapper = userDataParser.map {
        case id~name~icon~access_token~admin => UserDbData(id, name, icon, access_token, admin)
    }

    def addUser(id: Int, name: String, icon: Array[Byte], accessToken: String): Option[Long] = {
        db.withConnection { implicit connect =>
            SQL("INSERT INTO `user` (`id`, `name`, `icon`, `access_token`, admin) VALUES ({id}, {name}, {icon}, {access_token}, 0);")
                .on("id" -> id, "name" -> name, "icon" -> icon, "access_token" -> accessToken)
                .executeInsert()
        }
    }

    def getUser(id: Int): UserDbData = {
        db.withConnection{ implicit connect =>
            val data = SQL("SELECT * FROM `user` WHERE `id` = {id};")
                .on("id" -> id).as(userDataMapper.*)
            if (data.length < 1)
                null
            else
                data(0)
        }
    }

    def updateName(id: Int, name: String): Unit = {
        db.withConnection{ implicit connect =>
            SQL("UPDATE `user` SET `name` = {name} WHERE `id` = {id};")
                .on("name" -> name, "id" -> id)
                .executeUpdate()
        }
    }

    def updateIcon(id: Int, icon: Array[Byte]): Unit = {
        db.withConnection{ implicit connect =>
            SQL("UPDATE `user` SET `icon` = {icon} WHERE `id` = {id};")
                .on("icon" -> icon, "id" -> id)
                .executeUpdate()
        }
    }

    def updateAccessToken(id: Int, accessToken: String): Unit = {
        db.withConnection{ implicit connect =>
            SQL("UPDATE `user` SET `access_token` = {access_token} WHERE `id` = {id};")
                .on("access_token" -> accessToken, "id" -> id)
                .executeUpdate()
        }
    }
}
