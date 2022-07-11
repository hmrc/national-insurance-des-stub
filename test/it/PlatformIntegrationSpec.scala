/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it

import org.scalatest.OptionValues
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.nationalinsurancedesstub.controllers.DocumentationController

import scala.concurrent.Future
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/**
  * Testcase to verify the capability of integration with the API platform.
  *
  * 1a, To expose API's to Third Party Developers, the service needs to make the API definition available under api/definition GET endpoint
  * 1b, The endpoints need to be defined in an application.raml file for all versions  For all of the endpoints defined documentation will be provided and be
  * available under api/documentation/[version]/[endpoint name] GET endpoint
  * Example: api/documentation/1.0/Fetch-Some-Data
  */
class PlatformIntegrationSpec extends AnyWordSpecLike with GuiceOneAppPerTest with OptionValues with Matchers {

  trait Setup {
    val documentationController: DocumentationController = app.injector.instanceOf[DocumentationController]
    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  }

  "microservice" should {

    "provide definition endpoint" in new Setup {
      val result: Future[Result] = documentationController.definition().apply(request)
      status(result) shouldBe 200
    }

    "provide RAML conf endpoint" in new Setup {
      val result: Future[Result] = documentationController.raml("1.0", "application.raml")(request)
      status(result) shouldBe 200
    }
  }
}
