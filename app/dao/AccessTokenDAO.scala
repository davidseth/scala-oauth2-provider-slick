package dao

import java.util.UUID
import models.AccessToken
import scala.concurrent.{Future, ExecutionContext}

trait AccessTokenDAO {

  def create(accessToken: AccessToken)(implicit ec: ExecutionContext): Future[Unit]

  def deleteExistingAndCreate(accessToken: AccessToken, userGuid: UUID, clientId: Option[String])(implicit ec: ExecutionContext): Future[Unit]

  def findToken(userGuid: UUID, clientId: Option[String])(implicit ec: ExecutionContext): Future[Option[AccessToken]]

  def findAccessToken(token: String)(implicit ec: ExecutionContext): Future[Option[AccessToken]]

  def findRefreshToken(token: String)(implicit ec: ExecutionContext): Future[Option[AccessToken]]

}
