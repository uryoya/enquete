package controllers

import javax.inject._

import play.api.mvc._
import play.api.db._
import play.api.libs.ws.WSClient
import play.api.libs.json._
import dispatch._
import Defaults._
import databaseModels._


@Singleton
class AuthorizeController @Inject()(db: Database)(ws: WSClient) extends Controller {
    val clientId = "677bd149cb0df73ec46c"
    val clientSecret = "f940fe8e9f840f036a108859fe8691612f878d64"
    val userModel: UserModel = new UserModel(db)
    val pendingUserModel: PendingUserModel = new PendingUserModel(db)

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
        val accessToken: String = {
            val conn = url("https://github.com/login/oauth/access_token").POST
            .addHeader("Accept", "application/json")
            .addParameter("client_id", clientId)
            .addParameter("client_secret", clientSecret)
            .addParameter("code", sessionCode)
            // TODO: エラー処理を書いていないので追加する。
            val result = Http.default(conn OK as.String)
            (Json.parse(result()) \ "access_token").as[String] // as[T]による変換は安全ではない(今は実験
        }

        // Userデータを取得する
        val userJson: JsValue = {
            val conn = url("https://api.github.com/user")
                .addHeader("Authorization", "token " + accessToken)
            val result = Http.default(conn OK as.String)
            Json.parse(result())
        }

        // TODO: ユーザが存在しなかった場合の処理を書いてないやばい
        val githubId = (userJson \ "id").asOpt[Int] match {
            case Some(id) => id
            case None => 0
        }
        val githubName = (userJson \ "name").asOpt[String] match {
            case Some(name) => name
            case None => ""
        }
        val githubIconUrl = (userJson \ "avatar_url").asOpt[String] match {
            case Some(avatarUrl) => avatarUrl
            case None => ""
        }
        val githubIcon = {
            val conn = url(githubIconUrl)
            val result = Http.default(conn OK as.Bytes)
            result()
        }
        if (userModel.getUser(githubId) == null) {
          if (pendingUserModel.getPendingUser(githubId) == null) {
            pendingUserModel.addPendingUser(githubId, githubName, accessToken)
          } else {
            Redirect(routes.AuthorizeController.pending())
          }
            userModel.addUser(githubId, githubName, githubIcon, accessToken)
        }
        // これで一応JSONをパースできるが、レスポンスがエラーの場合死ぬ
        Redirect(routes.EnqueteController.index()).withSession("login" -> githubId.toString)
    }

    def signout = Action {
        // TODO: 未実装
        Ok(views.html.authorize())
    }

  def pending = Action {
    OK("You are pending now. Please wait.")
  }
}
