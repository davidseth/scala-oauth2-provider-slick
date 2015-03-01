package dao

import models._

import scala.slick.lifted.TableQuery

object Tables {

  val Clients = TableQuery[Clients]
  val Users = TableQuery[Users]
  val GrantTypes = TableQuery[GrantTypes]
  val ClientGrantTypes = TableQuery[ClientGrantTypes]
  val AccessTokens = TableQuery[AccessTokens]
  val AuthCodes = TableQuery[AuthCodes]
  val ConfirmationTokens = TableQuery[ConfirmationTokens]

}