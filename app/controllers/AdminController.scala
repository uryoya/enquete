package controllers

import javax.inject._
import play.api.mvc._
import play.api.db._
import databaseModels._

@Singleton
class AdminController @Inject()(db: Database) extends Controller {
  val userModel: UserModel = new UserModel(db)
  val pendingUserModel: PendingUserModel = new PendingUserModel(db)

  def index = Action { implicit request =>
    request.session.get("login").map { userId =>
      val admin = userModel.getUser(userId.toInt)
      if (admin.admin == 1) {
        Ok(views.html.adminIndex(pendingUserModel.getAll))
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
        pendingUserModel.authorize(id)
        Ok("Authorize the new user")
      } else {
        Forbidden("require the admin permission.")
      }
    }.getOrElse {
      Forbidden("require the admin permission.")
    }
  }
}
