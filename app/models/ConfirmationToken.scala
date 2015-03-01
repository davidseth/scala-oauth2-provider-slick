package models

import java.util.UUID

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import com.github.tototoshi.slick.HsqldbJodaSupport._

case class ConfirmationToken(id: Option[Long] , uuid: UUID, email: String, creationTime: DateTime, expirationTime: DateTime, isSignUp: Boolean)

class ConfirmationTokens(tag: Tag) extends Table[ConfirmationToken](tag, "confirmation_tokens") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def uuid = column[UUID]("uuid")
  def email = column[String]("email")
  def creationTime = column[DateTime]("creation_time")
  def expirationTime = column[DateTime]("expiration_time")
  def isSignUp = column[Boolean]("is_sign_up")
  def * = (id.?, uuid, email, creationTime, expirationTime, isSignUp) <> (ConfirmationToken.tupled, ConfirmationToken.unapply)
}

