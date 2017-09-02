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
  val enqueteForm = Form(
    mapping(
      "author" -> text,
      "title" -> text,
      "description" -> text
    )(enqueteData.apply)(enqueteData.unapply)
  )
  val answerForm = Form(
    mapping(
      "author" -> text,
      "answer" -> text
    )(answerData.apply)(answerData.unapply)
  )
  val commentForm = Form(
    mapping(
      "author" -> text,
      "comment" -> text
    )(commentData.apply)(commentData.unapply)
  )

  def index = Action {
    val enquetes = enqueteModel.getAllEnquete
    Ok(views.html.enquetes(enquetes)(enqueteForm))
  }

  def addEnquete = Action { implicit request =>
    val enquete = enqueteForm.bindFromRequest.get
    val result = enqueteModel.addEnquete(enquete.author, enquete.title, enquete.description)
    result match {
      case Some(id) => Redirect(routes.EnqueteController.enquete(id.toInt))
      case None => Redirect(routes.EnqueteController.index())
    }
  }

  def enquete(id: Int) = Action {
    val enquete = enqueteModel.getEnqutete(id)
    val answers = enqueteModel.getAllAnswer(id)
    Ok(views.html.enquete(enquete, answers)(answerForm))
  }

  def addAnswer(enqueteId: Int) = Action { implicit request =>
    val answer = answerForm.bindFromRequest.get
    val result = enqueteModel.addAnswer(enqueteId, answer.author, answer.answer)
    result match {
      case Some(id) => Redirect(routes.EnqueteController.answer(enqueteId, id.toInt))
      case None => Redirect(routes.EnqueteController.enquete(enqueteId))
    }
  }

  def answer(enqueteId: Int, id: Int) = Action {
    val enquete = enqueteModel.getEnqutete(enqueteId)
    val answer = enqueteModel.getAnswer(enqueteId, id)
    val comments = enqueteModel.getAllComment(id)
    Ok(views.html.answer(enquete, answer, comments)(commentForm))
  }

  def addComment(enqueteId: Int, answerId: Int) = Action { implicit request =>
    val comment = commentForm.bindFromRequest.get
    enqueteModel.addComment(answerId, comment.author, comment.comment)
    Redirect(routes.EnqueteController.answer(enqueteId, answerId))
  }
}
