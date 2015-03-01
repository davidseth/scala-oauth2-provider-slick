package dao.psql

import java.util.UUID

import dao.Tables.{Users, ClientGrantTypes, GrantTypes, ConfirmationTokens}
import models.{User, ConfirmationToken}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.util.Try
import scala.concurrent.{Future, ExecutionContext}

object UserDAOPsql {
  def getUserByEmail(email: String)(implicit ec: ExecutionContext): Future[Try[Option[User]]] = Future {
    Try {
      DB.withSession { implicit session =>
        Users.filter(u => u.email === email).firstOption
      }
    }.recover {
      case e =>
        //Logger.error(s"Unable to get a user by email '$email': ${e.getMessage}")
        sys.error(s"Unable to get a user by email '$email': ${e.getMessage}")
    }
  }

  def getByGuid(guid: UUID)(implicit ec: ExecutionContext): Future[Try[Option[User]]] = Future {
    Try {
      DB.withSession { implicit session =>
        Users.filter(u => u.guid === guid).firstOption
      }
    }.recover {
      case e =>
        //Logger.error(s"Unable to get a user by guid '$guid': ${e.getMessage}")
        sys.error(s"Unable to get a user by guid '$guid': ${e.getMessage}")
    }
  }

  def createUser(user: User)(implicit ec: ExecutionContext): Future[Try[Option[User]]] = Future {
    Try {
      DB.withSession { implicit session =>
        val guid = (Users returning Users.map(_.guid)) += user
        Option(guid).map(_ => user)
      }
    }.recover {
      case e =>
        //Logger.error(s"Unable to create user '${user.email}': ${e.getMessage}")
        sys.error(s"Unable to create user '${user.email}': ${e.getMessage}")
    }
  }

  def createConfirmationToken(confirmationToken: ConfirmationToken)(implicit ec: ExecutionContext): Future[Try[Option[ConfirmationToken]]] = Future {
    Try {
      DB.withSession { implicit session =>
        val id = (ConfirmationTokens returning ConfirmationTokens.map(_.id)) += confirmationToken

        Some(confirmationToken.copy(id = Some(id)))
      }
    }.recover {
      case e =>
        //Logger.error(s"Unable create confirmation token for user '${confirmationToken.email}': ${e.getMessage}")
        sys.error(s"Unable create confirmation token for user '${confirmationToken.email}': ${e.getMessage}")
    }
  }

  def deleteConfirmationToken(uuid: UUID)(implicit ec: ExecutionContext): Future[Try[Option[ConfirmationToken]]] = Future {
    Try {
      DB.withSession { implicit session =>
        val tokenResults = ConfirmationTokens.filter(_.uuid === uuid)
        tokenResults.list match {
          case List() => None
          case List(t) =>
            tokenResults.delete
            Some(ConfirmationToken(t.id, t.uuid, t.email, t.creationTime, t.expirationTime, t.isSignUp))

          case l =>
            //Logger.error(s"Should have found only one token with uuid $uuid, but found $l")
            sys.error(s"Should have found only one token with uuid $uuid, but found $l")
        }
      }
    }.recover {
      case e =>
        //Logger.error(s"Unable delete confirmation token '$uuid': ${e.getMessage}")
        sys.error(s"Unable delete confirmation token '$uuid': ${e.getMessage}")
    }
  }

  def getConfirmationTokenByUUID(uuid: UUID)(implicit ec: ExecutionContext): Future[Try[Option[ConfirmationToken]]] = Future {
    Try {
      DB.withSession { implicit session =>
        ConfirmationTokens.filter(_.uuid === uuid).list match {
          case List() => None
          case List(t) => Some(ConfirmationToken(t.id, t.uuid, t.email, t.creationTime, t.expirationTime, t.isSignUp))
          case l =>
            //Logger.error(s"Should have found only one token with uuid $uuid, but found $l")
            sys.error(s"Should have found only one token with uuid $uuid, but found $l")
        }
      }
    }.recover {
      case e =>
        //Logger.error(s"Unable to get token for uuid $uuid, ${e.getMessage}")
        sys.error(s"Unable to get token for uuid $uuid, ${e.getMessage}")
    }
  }
}
