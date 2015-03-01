package controllers

import init.Oauth2DataHandler
import init.MyTokenEndpoint
import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaoauth2.provider._

import controllers._

trait MyOAuth extends OAuth2Provider {
  override val tokenEndpoint: TokenEndpoint = MyTokenEndpoint
}

object OAuth2Controller extends Controller with MyOAuth {
  def accessToken = Action.async { implicit request =>
    issueAccessToken(new Oauth2DataHandler())
  }
}


