package models

import java.util.UUID

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import com.github.tototoshi.slick.HsqldbJodaSupport._

case class GrantType(id: Int, grantType: String)

class GrantTypes(tag: Tag) extends Table[GrantType](tag, "grant_types") {
  def id = column[Int]("id", O.PrimaryKey)
  def grantType = column[String]("grant_type")
  def * = (id, grantType) <> (GrantType.tupled, GrantType.unapply)
}