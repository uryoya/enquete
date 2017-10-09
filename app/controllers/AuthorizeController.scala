package controllers

import javax.inject._

import akka.stream.javadsl.Source
import play.api.Play._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.db._
import play.api._
import formModels._
import databaseModels._
import play.api.http.Writeable
import play.api.libs.json.JsResult
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.DataPart

import scala.concurrent.Future


@Singleton
class AuthorizeController @Inject()(db: Database)(ws: WSClient) extends Controller {
  val clientId = "677bd149cb0df73ec46c"
  val clientSecret = "f940fe8e9f840f036a108859fe8691612f878d64"

  def signin = Action { implicit request =>
    Ok(views.html.authorize_signin(clientId))
  }

  def callback = Action { implicit request =>
    val sessionCode = request.getQueryString("code")
    // val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
    case class AccessToken(accesssToken: String, Scope: String, tokenType: String)
    val result: Future[JsResult[AccessToken]] = ws.url("https://github.com/login/oauth/access_token")
      .withHeaders("Accept" -> "application/json")
      .post(Writeable(1)).map {
      response => (response.json).validate[AccessToken]
    }
//    Map(
//        "client_id" -> Seq(clientId),
//        "client_secret" -> Seq(clientSecret),
//        "code" -> Seq(sessionCode)
//      )
    result.foreach{
      case a: AccessToken => Ok(views.html.authorize_atoken(a.accesssToken))
      case _ => Ok(views.html.authorize())
    }
    Ok(views.html.authorize())
  }

  def signout = Action {
    Ok(views.html.authorize())
  }
}
