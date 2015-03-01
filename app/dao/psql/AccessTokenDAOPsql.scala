package dao.psql

import java.util.UUID

import dao.Tables.AccessTokens
import models.AccessToken
import play.api.Play.current
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag

import scala.concurrent.{Future, ExecutionContext}


object AccessTokenDAOPsql extends dao.AccessTokenDAO {
  override def create(accessToken: AccessToken)(implicit ec: ExecutionContext): Future[Unit] = Future {
    DB.withSession { implicit session =>
      AccessTokens.insert(accessToken)
    }
  }

  override def deleteExistingAndCreate(accessToken: AccessToken,
                                       userGuid: UUID,
                                       clientId: Option[String])(implicit ec: ExecutionContext): Future[Unit] = Future {
    DB.withSession { implicit session =>
      // these two operations should happen inside a transaction
      AccessTokens.filter(a => a.clientId === clientId && a.userGuid === userGuid).delete
      AccessTokens.insert(accessToken)
    }
  }

  override def findToken(userGuid: UUID, clientId: Option[String])
                        (implicit ec: ExecutionContext): Future[Option[AccessToken]] = Future {
    DB.withSession { implicit session =>
      AccessTokens.filter(a => a.clientId === clientId && a.userGuid === userGuid).firstOption
    }
  }

  override def findAccessToken(token: String)(implicit ec: ExecutionContext): Future[Option[AccessToken]] = Future {
    DB.withSession { implicit session =>
      AccessTokens.filter(a => a.accessToken === token).firstOption
    }
  }

  override def findRefreshToken(token: String)(implicit ec: ExecutionContext): Future[Option[AccessToken]] = Future {
    DB.withSession { implicit session =>
      AccessTokens.filter(a => a.refreshToken === token).firstOption
    }
  }
}
