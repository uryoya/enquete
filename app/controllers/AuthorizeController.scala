package controllers

import javax.inject._

import play.api.mvc._
import play.api.db._
import play.api.libs.ws.WSClient
import play.api.libs.json.Json
import dispatch._
import Defaults._
import org.asynchttpclient.HttpResponseBodyPart

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
        // GitHub OAuth認証の流れ
        // 1. authorizeUrlにユーザがアクセスして、GitHubでログインする。認証を得るレベル(scope)はURLから決定される。
        // 2. GitHubでユーザがログインすると、AuthorizeController.callback()にリダイレクトされる。 -> callback()
        val authorizeUrl = "https://github.com/login/oauth/authorize?scope=user:email&client_id=" + clientId
        Ok(views.html.authorize_signin(authorizeUrl))
    }

    def callback = Action { implicit request =>
        // 3. GitHubからは、コールバックしてくるURLにクエリとしてアクセスコードが含められる。
        // 4. アクセスコード、client_id、client_secretを含めてリクエストをすると、access_tokenが得られる。
        val sessionCode = request.getQueryString("code").getOrElse("")
        println(sessionCode)
        val conn = url("https://github.com/login/oauth/access_token").POST
            .addHeader("Accept", "application/json")
            .addParameter("client_id", clientId)
            .addParameter("client_secret", clientSecret)
            .addParameter("code", sessionCode)
        // TODO: エラー処理を書いていないので追加する。
        val accessToken = Http.default(conn OK as.String)
        val result = accessToken()
        val accessTokenJson = Json.fromJson[AccessToken](Json.parse(result)).get
        // これで一応JSONをパースできるが、レスポンスがエラーの場合死ぬ
        Redirect(routes.EnqueteController.index())
    }

    def signout = Action {
        // TODO: 未実装
        Ok(views.html.authorize())
    }
}
