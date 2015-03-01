package controllers

import java.util.UUID
import controllers.Application._
import org.joda.time.DateTime

import models._
import org.mindrot.jbcrypt.BCrypt
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits._

import dao.Tables.Users

object UserController extends Controller {

  //JSON read/write macro
  implicit val userFormat = Json.format[User]

//  case class CreateUser2(email: String, password: String, firstName: Option[String], lastName: Option[String])
//
//  implicit def createUserReads2: Reads[CreateUser2] = {
//    (
//      (__ \ "email").read[String] and
//        (__ \ "password").read[String] and
//        (__ \ "firstName").readNullable[String] and
//        (__ \ "lastName").readNullable[String]
//      )(CreateUser2.apply _)
//  }
//
//  implicit def createUserWrites2: play.api.libs.json.Writes[CreateUser2] = {
//    (
//      (__ \ "email").write[String] and
//        (__ \ "password").write[String] and
//        (__ \ "firstName").write[Option[String]] and
//        (__ \ "lastName").write[Option[String]]
//      )(unlift(CreateUser2.unapply _))
//  }

  case class CreateUser(email: String, password: String, firstName: String, lastName: String)

  implicit def createUserReads: Reads[CreateUser] = {
    (
      (__ \ "email").read[String] and
        (__ \ "password").read[String] and
        (__ \ "firstName").read[String] and
        (__ \ "lastName").read[String]
      )(CreateUser.apply _)
  }

  implicit def createUserWrites: play.api.libs.json.Writes[CreateUser] = {
    (
      (__ \ "email").write[String] and
        (__ \ "password").write[String] and
        (__ \ "firstName").write[String] and
        (__ \ "lastName").write[String]
      )(unlift(CreateUser.unapply _))
  }

  def jsonInsert = DBAction(parse.json) { implicit request =>

    //implicit val createUserFormat = Json.format[CreateUser]
    //val user = request.body.validate[CreateUser]

    request.request.body.validate[CreateUser].map { createdUser =>
      saveUser(createdUser)

      Ok(toJson(createdUser))
    }.getOrElse(BadRequest("invalid json"))
  }

  /**
   * Encrypt the clear password using b-crypt
   *
   * @param password  the clear password to encrypt
   * @return          the hashed password and the salt used
   */
  def encryptPassword(password: String): (String, Option[String]) = {
    val salt = BCrypt.gensalt(10)
    val hash = BCrypt.hashpw(password, salt)
    (hash, Some(salt))
  }

  def jsonFindAll = DBAction { implicit rs =>
    Ok(toJson(Users.list))
  }

  def saveUser(user : CreateUser) = {
    val (hash, salt) = encryptPassword(user.password)
    val now = DateTime.now
    val uuid = UUID.randomUUID
    val u = User(java.util.UUID.randomUUID, user.firstName, user.lastName, user.email, hash, salt, now, uuid, now, uuid, None, None)

    dao.psql.UserDAOPsql.createUser(u)

  }
}