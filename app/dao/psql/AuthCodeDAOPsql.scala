package dao.psql

import java.util.UUID

import dao.Tables.AuthCodes
import models.AuthCode
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag

import scala.concurrent.{Future, ExecutionContext}

object AuthCodeDAOPsql {
  def find(code: String)(implicit ec: ExecutionContext): Future[Option[AuthCode]] = Future {
    DB.withSession { implicit session =>
      val authCode = AuthCodes.filter(a => a.authorizationCode === code).firstOption

      // filtering out expired authorization codes
      authCode.filter(p => p.createdAt.getMillis + (p.expiresIn * 1000) > DateTime.now.getMillis)
    }
  }
}
