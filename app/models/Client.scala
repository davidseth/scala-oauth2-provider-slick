package models

import java.util.UUID

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import com.github.tototoshi.slick.HsqldbJodaSupport._

case class Client(id: String, secret: Option[String], redirectUri: Option[String], scope: Option[String])

class Clients(tag: Tag) extends Table[Client](tag, "clients") {
  def id = column[String]("id", O.PrimaryKey)
  def secret = column[Option[String]]("secret")
  def redirectUri = column[Option[String]]("redirect_uri")
  def scope = column[Option[String]]("scope")
  def * = (id, secret, redirectUri, scope) <> (Client.tupled, Client.unapply)
}

