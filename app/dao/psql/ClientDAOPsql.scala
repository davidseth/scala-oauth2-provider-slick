package dao.psql

import java.util.UUID

import dao.Tables.{Clients, ClientGrantTypes, GrantTypes}
import models.{Client, ClientGrantType}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import scala.concurrent.{Future, ExecutionContext}

object ClientDAOPsql {
  def validate(id: String, secret: Option[String], grantType: String)
                       (implicit ec: ExecutionContext): Future[Boolean] = Future {
    DB.withSession { implicit session =>
      val check = for {
        ((c, cgt), gt) <- Clients innerJoin ClientGrantTypes on (_.id === _.clientId) innerJoin GrantTypes on (_._2.grantTypeId === _.id)
        if c.id === id && c.secret === secret && gt.grantType === grantType
      } yield c
      check.firstOption.isDefined
    }
  }

  def findById(id: String)(implicit ec: ExecutionContext): Future[Option[Client]] = Future {
    DB.withSession { implicit session =>
      Clients.filter(c => c.id === id).firstOption
    }
  }
}
