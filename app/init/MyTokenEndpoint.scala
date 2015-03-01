package init

import scalaoauth2.provider.{Password, TokenEndpoint}


class MyTokenEndpoint extends TokenEndpoint {
  val passwordNoCred = new Password() {
    override def clientCredentialRequired = false
  }

  override val handlers = Map(
    "password" -> passwordNoCred
  )
}

object MyTokenEndpoint extends MyTokenEndpoint
