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
  val userModel: Usermodel = new UserModel(db)
}
