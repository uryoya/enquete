package controllers

import javax.inject._
import play.api.Play._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.db._
import formModels._
import databaseModels._

@Singleton
class EnqueteController @Inject()(db: Database) extends Controller {
    val enqueteModel: EnquetesModel = new EnquetesModel(db)
    val userModel: UserModel = new UserModel(db)
    val enqueteForm = Form(
        mapping(
            "title" -> text,
            "description" -> text
        )(enqueteData.apply)(enqueteData.unapply)
    )
    val answerForm = Form(
        mapping(
            "answer" -> text
        )(answerData.apply)(answerData.unapply)
    )
    val commentForm = Form(
        mapping(
            "comment" -> text
        )(commentData.apply)(commentData.unapply)
    )

    def index = Action { implicit request =>
        request.session.get("login").map { userId =>
            val enquetes = enqueteModel.getAllEnquete
            Ok(views.html.enquetes(enquetes)(enqueteForm))
        }.getOrElse {
            Redirect(routes.AuthorizeController.signin())
        }
    }

    def addEnquete = Action { implicit request =>
        request.session.get("login").map { userId =>
            val enquete = enqueteForm.bindFromRequest.get
            val result = enqueteModel.addEnquete(userId.toInt, enquete.title, enquete.description)
            result match {
                case Some(id) => Redirect(routes.EnqueteController.enquete(id.toInt))
                case None => Redirect(routes.EnqueteController.index())
            }
        }.getOrElse {
            Redirect(routes.AuthorizeController.signin())
        }
    }

    def enquete(id: Int) = Action { implicit request =>
        request.session.get("login").map { userId =>
            val enquete = enqueteModel.getEnqutete(id)
            val answers = enqueteModel.getAllAnswer(id)
            Ok(views.html.enquete(id)(enquete, answers)(answerForm))
        }.getOrElse {
            Redirect(routes.AuthorizeController.signin())
        }
    }

    def addAnswer(enqueteId: Int) = Action { implicit request =>
        request.session.get("login").map { userId =>
            val answer = answerForm.bindFromRequest.get
            val result = enqueteModel.addAnswer(enqueteId, userId.toInt, answer.answer)
            result match {
                case Some(id) => Redirect(routes.EnqueteController.answer(enqueteId, id.toInt))
                case None => Redirect(routes.EnqueteController.enquete(enqueteId))
            }
        }.getOrElse {
            Redirect(routes.AuthorizeController.signin())
        }
    }

    def answer(enqueteId: Int, id: Int) = Action { implicit request =>
        request.session.get("login").map { userId =>
            val enquete = enqueteModel.getEnqutete(enqueteId)
            val answer = enqueteModel.getAnswer(enqueteId, id)
            val comments = enqueteModel.getAllComment(id)
            Ok(views.html.answer(enqueteId, id)(enquete, answer, comments)(commentForm))
        }.getOrElse {
            Redirect(routes.AuthorizeController.signin())
        }
    }

    def addComment(enqueteId: Int, answerId: Int) = Action { implicit request =>
        request.session.get("login").map { userId =>
            val comment = commentForm.bindFromRequest.get
            enqueteModel.addComment(answerId, userId.toInt, comment.comment)
            Redirect(routes.EnqueteController.answer(enqueteId, answerId))
        }.getOrElse {
            Redirect(routes.AuthorizeController.signin())
        }
    }
}
