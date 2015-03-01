package models

import java.util.UUID

import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import com.github.tototoshi.slick.HsqldbJodaSupport._

import play.api.db.slick.Config.driver.simple._

case class User(guid: UUID,
                firstName: String,
                lastName: String,
                email: String,
                password: String,
                salt: Option[String],
                createdAt: DateTime,
                createdByGuid: UUID,
                updatedAt: DateTime,
                updatedByGuid: UUID,
                deletedAt: Option[DateTime],
                deletedByGuid: Option[UUID])

class Users(tag: Tag) extends Table[User](tag, "users") {
  def guid = column[UUID]("guid", O.PrimaryKey)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def email = column[String]("email")
  def password = column[String]("password")
  def salt = column[Option[String]]("salt")
  def createdAt = column[DateTime]("created_at")
  def createdByGuid = column[UUID]("created_by_guid")
  def updatedAt = column[DateTime]("updated_at")
  def updatedByGuid = column[UUID]("updated_by_guid")
  def deletedAt = column[Option[DateTime]]("deleted_at")
  def deletedByGuid = column[Option[UUID]]("deleted_by_guid")
  def * = (guid, firstName, lastName, email, password, salt, createdAt, createdByGuid, updatedAt, updatedByGuid, deletedAt, deletedByGuid) <> (User.tupled, User.unapply)
}