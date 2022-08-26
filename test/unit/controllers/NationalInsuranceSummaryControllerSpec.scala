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

package unit.controllers

import akka.stream.Materializer
import org.mockito.ArgumentMatchers.{eq => argEq}
import org.mockito.BDDMockito.given
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, _}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalinsurancedesstub.controllers.NationalInsuranceSummaryController
import uk.gov.hmrc.nationalinsurancedesstub.models._
import uk.gov.hmrc.nationalinsurancedesstub.services.{NationalInsuranceSummaryService, ScenarioLoader}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NationalInsuranceSummaryControllerSpec
    extends AnyWordSpec
    with Matchers
    with OptionValues
    with MockitoSugar
    with GuiceOneServerPerSuite
    with ScalaFutures {

  override lazy val fakeApplication: Application = GuiceApplicationBuilder(
    disabled = Seq(classOf[com.kenshoo.play.metrics.PlayModule])
  ).build()

  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
  implicit val headerCarrier: HeaderCarrier        = HeaderCarrier()
  implicit val mat: Materializer                   = fakeApplication.materializer

  val underTest = new NationalInsuranceSummaryController(
    mock[ScenarioLoader],
    mock[NationalInsuranceSummaryService],
    stubControllerComponents()
  )

  def request(jsonPayload: JsValue): FakeRequest[JsValue]          =
    FakeRequest().withHeaders(HeaderNames.ACCEPT -> "application/vnd.hmrc.1.0+json").withBody[JsValue](jsonPayload)

  def createSummaryRequest(scenario: String): FakeRequest[JsValue] =
    request(Json.parse(s"""{ "scenario": "$scenario" }"""))

  def emptyRequest: FakeRequest[JsValue] =
    request(Json.parse("{}"))

  val nics = NICs(Class1NICs(10), Class2NICs(20), maxNICsReached = false)

  "fetch" should {

    "return the happy path response when called with a utr and tax year that are found" in {

      given(underTest.service.fetch(argEq("2234567890"), argEq("2014")))
        .willReturn(Future(Some(NationalInsuranceSummary("2234567890", "2014", nics))))

      val result: Future[Result] = underTest.fetch("2234567890", "2014")(request)

      status(result)        shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(nics)
    }

    "return a not found response when called with a utr and taxYear that are not found" in {

      given(underTest.service.fetch(argEq("2234567890"), argEq("2014"))).willReturn(Future(None))

      val result: Result = underTest.fetch("2234567890", "2014")(request).futureValue

      status(Future(result)) shouldBe NOT_FOUND
    }

    "return an invalid server error when the service fails" in {

      given(underTest.service.fetch(argEq("2234567890"), argEq("2014")))
        .willReturn(Future.failed(new RuntimeException("expected test error")))

      val result: Result = underTest.fetch("2234567890", "2014")(request).futureValue

      status(Future(result)) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "create" should {

    "return a created response and store the National Insurance summary" in {

      given(underTest.scenarioLoader.loadScenario(argEq("HAPPY_PATH_2"))).willReturn(Future.successful(nics))
      given(underTest.service.create(argEq("2234567890"), argEq("2015"), argEq(nics)))
        .willReturn(Future.successful(NationalInsuranceSummary("2234567890", "2015", nics)))

      val result: Result =
        underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(createSummaryRequest("HAPPY_PATH_2")).futureValue

      status(Future(result)) shouldBe CREATED

    }

    "default to Happy Path Scenario 1 when no scenario is specified in the request" in {

      given(underTest.scenarioLoader.loadScenario(argEq("HAPPY_PATH_1"))).willReturn(Future.successful(nics))
      given(underTest.service.create(argEq("2234567890"), argEq("2015"), argEq(nics)))
        .willReturn(Future.successful(NationalInsuranceSummary("2234567890", "2015", nics)))

      val result: Result = underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(emptyRequest).futureValue

      status(Future(result)) shouldBe CREATED

    }

    "return an invalid server error when the repository fails" in {

      given(underTest.scenarioLoader.loadScenario(argEq("HAPPY_PATH_2"))).willReturn(Future.successful(nics))
      given(underTest.service.create(argEq("2234567890"), argEq("2015"), argEq(nics)))
        .willReturn(Future.failed(new RuntimeException("expected test error")))

      val result: Future[Result] =
        underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(createSummaryRequest("HAPPY_PATH_2"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "return a bad request when the scenario is invalid" in {

      given(underTest.scenarioLoader.loadScenario(argEq("INVALID")))
        .willReturn(Future.failed(new InvalidScenarioException("INVALID")))

      val result: Future[Result] =
        underTest.create(SaUtr("2234567890"), TaxYear("2014-15"))(createSummaryRequest("INVALID"))

      status(result)                              shouldBe BAD_REQUEST
      (contentAsJson(result) \ "code").as[String] shouldBe "UNKNOWN_SCENARIO"
    }
  }
}
