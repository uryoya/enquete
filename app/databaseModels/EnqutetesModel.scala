package databaseModels

import javax.inject._

import play.api.db._
import anorm._
import anorm.SqlParser._

case class EnqueteDbData(id: Int, userId: Int, title: String, description: String)
case class AnswerDbData(id: Int, userId: Int, answer: String, enqueteId: Int)
case class CommentDbData(id: Int, userId: Int, comment: String, answerId: Int)

@Singleton
class EnquetesModel(db: Database) {
  // enquete parser
  val enqueteDataParser = int("id") ~ int("user_id") ~ str("title") ~ str("description")
  val enqueteDataMapper = enqueteDataParser.map {
    case id~user_id~title~description => EnqueteDbData(id, user_id, title, description)
  }
  // answer parser
  val answerDataParser = int("id") ~ int("user_id") ~ str("answer") ~ int("enquete_id")
  val answerDataMapper = answerDataParser.map {
    case id~user_id~answer~enquete_id => AnswerDbData(id, user_id, answer, enquete_id)
  }
  // comment parser
  val commentDataParser = int("id") ~ int("user_id") ~ str("comment") ~ int("answer_id")
  val commentDataMapper = commentDataParser.map {
    case id~user_id~comment~answer_id => CommentDbData(id, user_id, comment, answer_id)
  }

  def addEnquete(user_id: Int, title: String, description: String): Option[Long] = {
    db.withConnection{ implicit connect =>
      SQL("INSERT INTO `enquete` (`user_id`, `title`, `description`) VALUES ({user_id}, {title}, {description});")
        .on("user_id" -> user_id, "title" -> title, "description" -> description)
        .executeInsert()
    }
  }

  def getEnqutete(id: Int): EnqueteDbData = {
    db.withConnection{ implicit connect =>
      val data = SQL("SELECT * FROM `enquete` WHERE `id` = {id};")
        .on("id" -> id).as(enqueteDataMapper.*)
      if (data.length < 1)
        null
      else
        data(0)
    }
  }

  def getAllEnquete: List[EnqueteDbData] = {
    db.withConnection{ implicit connect =>
      SQL("SELECT * FROM `enquete`").as(enqueteDataMapper.*)
    }
  }

  def addAnswer(enqueteId: Int, user_id: Int, answer: String): Option[Long] = {
    db.withConnection{ implicit connect =>
      SQL("INSERT INTO `answer` (`enquete_id`, `user_id`, `answer`) VALUES ({id}, {user_id}, {answer});")
        .on("id" -> enqueteId, "user_id" -> user_id, "answer" -> answer).executeInsert()
    }
  }

  def getAnswer(enqueteId: Int, id: Int): AnswerDbData = {
    db.withConnection{ implicit connect =>
      val data = SQL("SELECT * FROM `answer` WHERE `enquete_id` = {enquete_id} and `id` = {id};")
        .on("enquete_id" -> enqueteId, "id" -> id).as(answerDataMapper.*)
      if (data.length < 1)
        null
      else
        data(0)
    }
  }

  def getAllAnswer(enqueteId: Int): List[AnswerDbData] = {
    db.withConnection{ implicit connect =>
      SQL("SELECT * FROM `answer` WHERE `enquete_id` = {enquete_id};")
        .on("enquete_id" -> enqueteId).as(answerDataMapper.*)
    }
  }

  def addComment(answerId: Int, user_id: Int, comment: String): Option[Long] = {
    db.withConnection{ implicit connect =>
      SQL("INSERT INTO `comment` (`answer_id`, `user_id`, `comment`) VALUES ({id}, {user_id}, {comment});")
        .on("id" -> answerId, "user_id" -> user_id, "comment" -> comment).executeInsert()
    }
  }

  def getComment(answerId: Int, id: Int): CommentDbData = {
    db.withConnection{ implicit connect =>
      val data = SQL("SELECT * FROM `comment` WHERE `answer_id` = {answer_id} and `id` = {id};")
        .on("answer_id" -> answerId, "id" -> id).as(commentDataMapper.*)
      if (data.length < 1)
        null
      else
        data(0)
    }
  }

  def getAllComment(answerId: Int): List[CommentDbData] = {
    db.withConnection{ implicit connect =>
      SQL("SELECT * FROM `comment` WHERE `answer_id` = {answer_id};")
        .on("answer_id" -> answerId).as(commentDataMapper.*)
    }
  }
}
