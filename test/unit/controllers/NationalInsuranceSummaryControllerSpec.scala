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

package unit.controllers

import org.mockito.Mockito.verify
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.BDDMockito.given
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import common.util.ResourceLoader._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.nationalinsurancedesstub.controllers.NationalInsuranceSummaryController
import uk.gov.hmrc.nationalinsurancedesstub.models.NationalInsuranceSummary
import uk.gov.hmrc.nationalinsurancedesstub.repositories.NationalInsuranceSummaryRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class NationalInsuranceSummaryControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  trait Setup extends MicroserviceFilterSupport {
    val request = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    implicit val headerCarrier = HeaderCarrier()

    val underTest = new NationalInsuranceSummaryController {
      override val repository = mock[NationalInsuranceSummaryRepository]
    }
  }

  "fetch" should {

    "return the happy path response when called with a utr and tax year that are found" in new Setup {

      given(underTest.repository.fetch("2234567890", "2014")).willReturn(Future(Some(NationalInsuranceSummary("2234567890", "2014"))))
      val expected = loadResource("/public/national-insurance-summary.json")

      val result = await(underTest.fetch("2234567890", "2014")(request))

      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe Json.parse(expected)
    }

    "return a not found response when called with a utr and taxYear that are not found" in new Setup {

      given(underTest.repository.fetch("2234567890", "2014")).willReturn(Future(None))

      val result = await(underTest.fetch("2234567890", "2014")(request))

      status(result) shouldBe NOT_FOUND
    }

    "return an invalid server error when the repository fails" in new Setup {

      given(underTest.repository.fetch(anyString, anyString)).willReturn(Future.failed(new RuntimeException("expected test error")))

      val result = await(underTest.fetch("2234567890", "2014")(request))

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "create" should {

    "return a created response and store the utr and tax year" in new Setup {

      val nationalInsuranceSummary = NationalInsuranceSummary("2234567890", "2014")
      given(underTest.repository.store(nationalInsuranceSummary)).willReturn(Future.successful(nationalInsuranceSummary))

      val result = await(underTest.create("2234567890", "2014-15")(request))

      status(result) shouldBe CREATED
      verify(underTest.repository).store(NationalInsuranceSummary("2234567890", "2014"))
    }

    "return an invalid server error when the repository fails" in new Setup {

      given(underTest.repository.store(any)).willReturn(Future.failed(new RuntimeException("expected test error")))

      val result = await(underTest.create("2234567890", "2014-15")(request))

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
