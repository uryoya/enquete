package controllers

import javax.inject._

import play.api.mvc._
import play.api.db._
import play.api.libs.ws.WSClient
import play.api.libs.json.Json
import dispatch._, Defaults._

case class AccessToken(access_token: String, scope: String, token_type: String)
object AccessToken {
    implicit def jsonWrites = Json.writes[AccessToken]
    implicit def jsonReads = Json.reads[AccessToken]
}

@Singleton
class AuthorizeController @Inject()(db: Database)(ws: WSClient) extends Controller {
    val clientId = "677bd149cb0df73ec46c"
    val clientSecret = "f940fe8e9f840f036a108859fe8691612f878d64"

    def signin = Action { implicit request =>
        Ok(views.html.authorize_signin(clientId))
    }

    def callback = Action { implicit request =>
        val sessionCode = request.getQueryString("code").getOrElse("")
        val conn = url("https://github.com/login/oauth/access_token").POST
            .addHeader("Accept", "application/json")
            .addParameter("client_id", clientId)
            .addParameter("client_secret", clientSecret)
            .addParameter("code", sessionCode)
        val accessToken = Http.default(conn OK as.String)
        val result = accessToken()
        val accessTokenJson = Json.fromJson[AccessToken](Json.parse(result)).get
        Ok(views.html.authorize_atoken(accessTokenJson.access_token))
        // これで一応JSONをパースできるが、レスポンスがエラーの場合死ぬ
    }

    def signout = Action {
        Ok(views.html.authorize())
    }
}
