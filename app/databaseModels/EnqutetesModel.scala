package databaseModels

import javax.inject._

import play.api.db._
import anorm._
import anorm.SqlParser._

case class EnqueteDbData(id: Int, author: String, title: String, description: String)

case class AnswerDbData(id: Int, author: String, answer: String)

case class CommentDbData(id: Int, author: String, comment: String)

@Singleton
class EnquetesModel(db: Database) {
    // enquete parser
    val enqueteDataParser = int("id") ~ str("name") ~ str("title") ~ str("description")
    val enqueteDataMapper = enqueteDataParser.map {
        case id ~ author ~ title ~ description => EnqueteDbData(id, author, title, description)
    }
    // answer parser
    val answerDataParser = int("id") ~ str("name") ~ str("answer")
    val answerDataMapper = answerDataParser.map {
        case id ~ author ~ answer => AnswerDbData(id, author, answer)
    }
    // comment parser
    val commentDataParser = int("id") ~ str("name") ~ str("comment")
    val commentDataMapper = commentDataParser.map {
        case id ~ author ~ comment => CommentDbData(id, author, comment)
    }

    def addEnquete(user_id: Int, title: String, description: String): Option[Long] = {
        db.withConnection { implicit connect =>
            SQL("INSERT INTO `enquete` (`user_id`, `title`, `description`) VALUES ({user_id}, {title}, {description});")
                .on("user_id" -> user_id, "title" -> title, "description" -> description)
                .executeInsert()
        }
    }

    def getEnqutete(id: Int): EnqueteDbData = {
        db.withConnection { implicit connect =>
            val data = SQL("""
                SELECT e.id, u.name, e.title, e.description
                FROM enquete e LEFT JOIN user u ON e.user_id = u.id WHERE e.id = {id};
            """).on("id" -> id).as(enqueteDataMapper.*)
            if (data.length < 1)
                null
            else
                data(0)
        }
    }

    def getAllEnquete: List[EnqueteDbData] = {
        db.withConnection { implicit connect =>
            SQL("""
                SELECT e.id, u.name, e.title, e.description
                FROM enquete e LEFT JOIN user u ON e.user_id = u.id;
             """).as(enqueteDataMapper.*)
        }
    }

    def addAnswer(enqueteId: Int, user_id: Int, answer: String): Option[Long] = {
        db.withConnection { implicit connect =>
            SQL("INSERT INTO `answer` (`enquete_id`, `user_id`, `answer`) VALUES ({id}, {user_id}, {answer});")
                .on("id" -> enqueteId, "user_id" -> user_id, "answer" -> answer).executeInsert()
        }
    }

    def getAnswer(enqueteId: Int, id: Int): AnswerDbData = {
        db.withConnection { implicit connect =>
            val data = SQL("""
                SELECT a.id, u.name, a.answer
                FROM answer a LEFT JOIN user u ON a.user_id = u.id
                WHERE a.enquete_id = {enquete_id} AND a.id = {id};
            """).on("enquete_id" -> enqueteId, "id" -> id).as(answerDataMapper.*)
            if (data.length < 1)
                null
            else
                data(0)
        }
    }

    def getAllAnswer(enqueteId: Int): List[AnswerDbData] = {
        db.withConnection { implicit connect =>
            SQL("""
                SELECT a.id, u.name, a.answer
                FROM answer a LEFT JOIN user u ON a.user_id = u.id
                WHERE a.enquete_id = {enquete_id};
            """).on("enquete_id" -> enqueteId).as(answerDataMapper.*)
        }
    }

    def addComment(answerId: Int, user_id: Int, comment: String): Option[Long] = {
        db.withConnection { implicit connect =>
            SQL("INSERT INTO `comment` (`answer_id`, `user_id`, `comment`) VALUES ({id}, {user_id}, {comment});")
                .on("id" -> answerId, "user_id" -> user_id, "comment" -> comment).executeInsert()
        }
    }

    def getComment(answerId: Int, id: Int): CommentDbData = {
        db.withConnection { implicit connect =>
            val data = SQL("""
                SELECT c.id, u.name, c.comment
                FROM comment c LEFT JOIN user u on c.user_id = u.id
                WHERE c.answer_id = {answer_id} AND c.id` = {id};
            """).on("answer_id" -> answerId, "id" -> id).as(commentDataMapper.*)
            if (data.length < 1)
                null
            else
                data(0)
        }
    }

    def getAllComment(answerId: Int): List[CommentDbData] = {
        db.withConnection { implicit connect =>
            SQL("""
                SELECT c.id, u.name, c.comment
                FROM comment c LEFT JOIN user u on c.user_id = u.id
                WHERE c.answer_id = {answer_id};
            """).on("answer_id" -> answerId).as(commentDataMapper.*)
        }
    }
}
