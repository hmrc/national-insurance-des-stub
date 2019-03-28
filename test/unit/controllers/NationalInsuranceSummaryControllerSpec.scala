/*
 * Copyright 2019 HM Revenue & Customs
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

package unit.controllers

import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalinsurancedesstub.controllers.NationalInsuranceSummaryController
import uk.gov.hmrc.nationalinsurancedesstub.models._
import uk.gov.hmrc.nationalinsurancedesstub.services.{NationalInsuranceSummaryService, ScenarioLoader}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class NationalInsuranceSummaryControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  trait Setup {
    val request = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    implicit val headerCarrier = HeaderCarrier()
    implicit val mat = fakeApplication.materializer

    val underTest = new NationalInsuranceSummaryController(mock[ScenarioLoader], mock[NationalInsuranceSummaryService])

    def request(jsonPayload: JsValue) = {
      FakeRequest().withHeaders(HeaderNames.ACCEPT -> "application/vnd.hmrc.1.0+json").withBody[JsValue](jsonPayload)
    }

    def createSummaryRequest(scenario: String) = {
      request(Json.parse(s"""{ "scenario": "$scenario" }"""))
    }

    def emptyRequest = {
      request(Json.parse("{}"))
    }

    val nics = NICs(Class1NICs(10), Class2NICs(20), maxNICsReached = false)
  }

  "fetch" should {

    "return the happy path response when called with a utr and tax year that are found" in new Setup {

      given(underTest.service.fetch(anyString, anyString)).willReturn(Future(Some(NationalInsuranceSummary("2234567890", "2014", nics))))

      val result = await(underTest.fetch("2234567890", "2014")(request))

      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe Json.toJson(nics)
    }

    "return a not found response when called with a utr and taxYear that are not found" in new Setup {

      given(underTest.service.fetch(anyString, anyString)).willReturn(Future(None))

      val result = await(underTest.fetch("2234567890", "2014")(request))

      status(result) shouldBe NOT_FOUND
    }

    "return an invalid server error when the service fails" in new Setup {

      given(underTest.service.fetch(anyString, anyString)).willReturn(Future.failed(new RuntimeException("expected test error")))

      val result = await(underTest.fetch("2234567890", "2014")(request))

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "create" should {

    "return a created response and store the National Insurance summary" in new Setup {

      given(underTest.scenarioLoader.loadScenario(anyString)).willReturn(Future.successful(nics))
      given(underTest.service.create(anyString, anyString, any[NICs])).willReturn(Future.successful(NationalInsuranceSummary("2234567890", "2015", nics)))

      val result = await(underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(createSummaryRequest("HAPPY_PATH_2")))

      status(result) shouldBe CREATED
      verify(underTest.scenarioLoader).loadScenario("HAPPY_PATH_2")
      verify(underTest.service).create("2234567890", "2015", nics)
    }

    "default to Happy Path Scenario 1 when no scenario is specified in the request" in new Setup {

      given(underTest.scenarioLoader.loadScenario(anyString)).willReturn(Future.successful(nics))
      given(underTest.service.create(anyString, anyString, any[NICs])).willReturn(Future.successful(NationalInsuranceSummary("2234567890", "2015", nics)))

      val result = await(underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(emptyRequest))

      status(result) shouldBe CREATED
      verify(underTest.scenarioLoader).loadScenario("HAPPY_PATH_1")
      verify(underTest.service).create("2234567890", "2015", nics)
    }

    "return an invalid server error when the repository fails" in new Setup {

      given(underTest.scenarioLoader.loadScenario(anyString)).willReturn(Future.successful(nics))
      given(underTest.service.create(anyString, anyString, any[NICs])).willReturn(Future.failed(new RuntimeException("expected test error")))

      val result = await(underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(createSummaryRequest("HAPPY_PATH_2")))

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "return a bad request when the scenario is invalid" in new Setup {

      given(underTest.scenarioLoader.loadScenario(anyString)).willReturn(Future.failed(new InvalidScenarioException("INVALID")))

      val result = await(underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(createSummaryRequest("INVALID")))

      status(result) shouldBe BAD_REQUEST
      (jsonBodyOf(result) \ "code").as[String] shouldBe "UNKNOWN_SCENARIO"
    }
  }
}
