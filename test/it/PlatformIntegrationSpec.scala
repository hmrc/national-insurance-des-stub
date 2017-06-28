/*
 * Copyright 2017 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, TestData}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.LazyHttpErrorHandler
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.{Application, Mode}
import uk.gov.hmrc.api.domain.Registration
import uk.gov.hmrc.nationalinsurancedesstub.config.AppContext
import uk.gov.hmrc.nationalinsurancedesstub.controllers.DocumentationController
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.test.UnitSpec

/**
  * Testcase to verify the capability of integration with the API platform.
  *
  * 1, To integrate with API platform the service needs to register itself to the service locator by calling the /registration endpoint and providing
  * - application name
  * - application url
  *
  * 2a, To expose API's to Third Party Developers, the service needs to make the API definition available under api/definition GET endpoint
  * 2b, The endpoints need to be defined in an application.raml file for all versions  For all of the endpoints defined documentation will be provided and be available under api/documentation/[version]/[endpoint name] GET endpoint
  * Example: api/documentation/1.0/Fetch-Some-Data
  *
  * See: https://confluence.tools.tax.service.gov.uk/display/ApiPlatform/API+Platform+Architecture+with+Flows
  */
class PlatformIntegrationSpec extends UnitSpec with MockitoSugar with ScalaFutures with BeforeAndAfterEach with GuiceOneAppPerTest {

  val stubHost = "localhost"
  val stubPort = sys.env.getOrElse("WIREMOCK_SERVICE_LOCATOR_PORT", "11111").toInt
  val wireMockServer = new WireMockServer(wireMockConfig().port(stubPort))

  override def newAppForTest(testData: TestData): Application = GuiceApplicationBuilder()
    .configure("run.mode" -> "Stub")
    .configure(Map(
      "appName" -> "application-name",
      "appUrl" -> "http://microservice-name.protected.mdtp",
      "Test.microservice.services.service-locator.host" -> stubHost,
      "Test.microservice.services.service-locator.port" -> stubPort,
      "Test.microservice.services.service-locator.enabled" -> true
    )).in(Mode.Test).build()

  override def beforeEach() {
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
    stubFor(post(urlMatching("/registration")).willReturn(aResponse().withStatus(204)))
  }

  trait Setup extends MicroserviceFilterSupport {
    val documentationController = new DocumentationController(LazyHttpErrorHandler, new AppContext()) {}
    val request = FakeRequest()
  }

  "microservice" should {

    "register itelf to service-locator" in new Setup {
      def regPayloadStringFor(serviceName: String, serviceUrl: String): String =
        Json.toJson(Registration(serviceName, serviceUrl, Some(Map("third-party-api" -> "true")))).toString

      verify(1, postRequestedFor(urlMatching("/registration")).
        withHeader("content-type", equalTo("application/json")).
        withRequestBody(equalTo(regPayloadStringFor("application-name", "http://microservice-name.protected.mdtp"))))
    }

    "provide definition endpoint" in new Setup {
      val result = documentationController.definition()(request)
      status(result) shouldBe 200
    }

    "provide RAML conf endpoint" in new Setup {
      val result = documentationController.raml("1.0", "application.raml")(request)
      status(result) shouldBe 200
    }
  }

  override protected def afterEach() = {
    wireMockServer.stop()
    wireMockServer.resetMappings()
  }
}
