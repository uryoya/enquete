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
class AdminController @Inject()(db: Database) extends Controller {
  val userModel: UserModel = new UserModel(db)

  def index = Action { implicit request =>
    request.session.get("login").map { userId =>
      val admin = userModel.getUser(userId.toInt)
      if (admin.admin == 1) {
        Ok("Admin's index page")
      } else {
        Redirect(routes.EnqueteController.index())
      }
    }.getOrElse {
      Redirect(routes.AuthorizeController.signin())
    }
  }

  def authorizeUser(id: Int) = Action { implicit request =>
    request.session.get("login").map { userId =>
      val admin = userModel.getUser(userId.toInt)
      if (admin.admin == 1) {
        Ok("Authorize the new user")
      } else {
        Redirect(routes.EnqueteController.index())
      }
    }.getOrElse {
      Redirect(routes.AuthorizeController.signin())
    }
  }
}
