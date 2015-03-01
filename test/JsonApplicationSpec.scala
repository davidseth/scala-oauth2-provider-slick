package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class JsonApplicationSpec extends Specification {

  "Application" should {

    "render the index page" in new WithApplication {

      val kitty = Json.obj("name" -> "Scarlett", "color" -> "Black & White")
      val postRequest = FakeRequest(
                  method = "POST",
                  uri = "/json/insert",
                  headers = FakeHeaders(
                    Seq("Content-type"->Seq("application/json"))
                  ),
                  body =  kitty
                )
      val Some(result) = route(postRequest)
      status(result) must equalTo(OK)

      val home = route(FakeRequest(GET, "/json/all")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      contentAsString(home) must contain ("Scarlett")
    }
  }
}