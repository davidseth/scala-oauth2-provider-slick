package init

import models.{AccessToken, User}
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future
import scala.util.Success
import scalaoauth2.provider.{AuthorizationHandler, DataHandler, AuthInfo, ClientCredential}
import scala.concurrent.{Future, ExecutionContext}

import dao.psql._




class Oauth2DataHandler extends DataHandler[User] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def findUser(email: String, password: String): Future[Option[User]] = {
    UserDAOPsql.getUserByEmail(email).map {
      case Success(optUser) =>
        optUser.filter(u => BCrypt.checkpw(password, u.password))
      case _ =>
        None
    }
  }

  override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =  {
    ClientDAOPsql.validate(clientCredential.clientId, clientCredential.clientSecret, grantType)
  }

  override def createAccessToken(authInfo: AuthInfo[User]): Future[scalaoauth2.provider.AccessToken] = {
    val accessTokenExpiresIn = Some(60L * 60L) // 1 hour
    val refreshToken = Some(Crypto.generateToken)
    val accessToken = Crypto.generateToken
    val now = DateTime.now()

    val tokenObject = AccessToken(accessToken, refreshToken, authInfo.user.guid, authInfo.scope, accessTokenExpiresIn, now, authInfo.clientId)
    AccessTokenDAOPsql.deleteExistingAndCreate(tokenObject, authInfo.user.guid, authInfo.clientId)
    Future.successful(scalaoauth2.provider.AccessToken(accessToken, refreshToken, authInfo.scope, accessTokenExpiresIn, now.toDate))
  }

  override def getStoredAccessToken(authInfo: AuthInfo[User]): Future[Option[scalaoauth2.provider.AccessToken]] = {
    AccessTokenDAOPsql.findToken(authInfo.user.guid, authInfo.clientId).map { optToken =>
      optToken.map { token =>
        scalaoauth2.provider.AccessToken(token.accessToken, token.refreshToken, token.scope, token.expiresIn, token.createdAt.toDate)
      }
    }
  }

  override def refreshAccessToken(authInfo: AuthInfo[User], refreshToken: String): Future[scalaoauth2.provider.AccessToken] = {
    createAccessToken(authInfo)
  }

  // TODO not implemented yet
  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[User]] = Future.successful(None)

  def findAccessToken(token: String): Future[Option[scalaoauth2.provider.AccessToken]] = {
    AccessTokenDAOPsql.findAccessToken(token).map { optToken =>
      optToken.map { token =>
        scalaoauth2.provider.AccessToken(token.accessToken, token.refreshToken, token.scope, token.expiresIn, token.createdAt.toDate)
      }
    }
  }

  override def findAuthInfoByAccessToken(accessToken: scalaoauth2.provider.AccessToken): Future[Option[AuthInfo[User]]] = {
    AccessTokenDAOPsql.findAccessToken(accessToken.token).flatMap { optToken =>
      optToken.map { token =>
        UserDAOPsql.getByGuid(token.userGuid).map {
          case Success(user) =>
            Some(AuthInfo(user.get, token.clientId, token.scope, Some("")))
          case _ =>
            None
        }
      }.getOrElse(Future.successful(None))
    }
  }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[User]]] = {
    AccessTokenDAOPsql.findRefreshToken(refreshToken).flatMap { optToken =>
      optToken.map { token =>
        UserDAOPsql.getByGuid(token.userGuid).map {
          case Success(user) =>
            Some(AuthInfo(user.get, token.clientId, token.scope, Some("")))
          case _ =>
            None
        }
      }.getOrElse(Future.successful(None))
    }
  }

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[User]]] = {
    AuthCodeDAOPsql.find(code).flatMap { optCode =>
      optCode.map { token =>
        UserDAOPsql.getByGuid(token.userGuid).map {
          case Success(user) =>
            Some(AuthInfo(user.get, token.clientId, token.scope, token.redirectUri))
          case _ =>
            None
        }
      }.getOrElse(Future.successful(None))
    }
  }
}

object Crypto {
  def generateToken: String = {
    val key = java.util.UUID.randomUUID.toString
    new sun.misc.BASE64Encoder().encode(key.getBytes)
  }
}
