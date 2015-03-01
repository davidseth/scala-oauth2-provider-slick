package models

import java.util.UUID

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import com.github.tototoshi.slick.HsqldbJodaSupport._

case class AccessToken(accessToken: String, refreshToken: Option[String], userGuid: UUID, scope: Option[String], expiresIn: Option[Long], createdAt: DateTime, clientId: Option[String])

class AccessTokens(tag: Tag) extends Table[AccessToken](tag, "access_tokens") {
  def accessToken = column[String]("access_token", O.PrimaryKey)
  def refreshToken = column[Option[String]]("refresh_token")
  def userGuid = column[UUID]("user_guid")
  def scope = column[Option[String]]("scope")
  def expiresIn = column[Option[Long]]("expires_in")
  def createdAt = column[DateTime]("created_at")
  def clientId = column[Option[String]]("client_id")
  def * = (accessToken, refreshToken, userGuid, scope, expiresIn, createdAt, clientId) <> (AccessToken.tupled, AccessToken.unapply)
}