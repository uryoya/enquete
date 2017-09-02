package databaseModels

import javax.inject._

import play.api.db._
import anorm._
import anorm.SqlParser._

case class EnqueteDbData(id: Int, author: String, title: String, description: String)
case class AnswerDbData(id: Int, author: String, answer: String, enqueteId: Int)
case class CommentDbData(id: Int, author: String, comment: String, answerId: Int)

@Singleton
class EnquetesModel(db: Database) {
  // enquete parser
  val enqueteDataParser = int("id") ~ str("author") ~ str("title") ~ str("description")
  val enqueteDataMapper = enqueteDataParser.map {
    case id~author~title~description => EnqueteDbData(id, author, title, description)
  }
  // answer parser
  val answerDataParser = int("id") ~ str("author") ~ str("answer") ~ int("enquete_id")
  val answerDataMapper = answerDataParser.map {
    case id~author~answer~enquete_id => AnswerDbData(id, author, answer, enquete_id)
  }
  // comment parser
  val commentDataParser = int("id") ~ str("author") ~ str("comment") ~ int("answer_id")
  val commentDataMapper = commentDataParser.map {
    case id~author~comment~answer_id => CommentDbData(id, author, comment, answer_id)
  }

  def addEnquete(author: String, title: String, description: String): Option[Long] = {
    db.withConnection{ implicit connect =>
      SQL("INSERT INTO `enquete` (`author`, `title`, `description`) VALUES ({author}, {title}, {description});")
        .on("author" -> author, "title" -> title, "description" -> description)
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

  def addAnswer(enqueteId: Int, author: String, answer: String): Option[Long] = {
    db.withConnection{ implicit connect =>
      SQL("INSERT INTO `answer` (`enquete_id`, `author`, `answer`) VALUES ({id}, {author}, {answer});")
        .on("id" -> enqueteId, "author" -> author, "answer" -> answer).executeInsert()
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

  def addComment(answerId: Int, author: String, comment: String): Option[Long] = {
    db.withConnection{ implicit connect =>
      SQL("INSERT INTO `comment` (`answer_id`, `author`, `comment`) VALUES ({id}, {author}, {comment});")
        .on("id" -> answerId, "author" -> author, "comment" -> comment).executeInsert()
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
