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

package unit

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.nationalinsurancedesstub.ErrorHandler
import uk.gov.hmrc.nationalinsurancedesstub.controllers.ErrorResponse
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent

import scala.concurrent.ExecutionContext

class ErrorHandlerSpec extends AnyWordSpec with MockitoSugar with Matchers {

  def versionHeader: (String, String) = ACCEPT -> "application/vnd.hmrc.1.0+json"

  private trait Test {
    val requestHeader: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(versionHeader)

    val auditConnector: AuditConnector = mock[AuditConnector]
    val httpAuditEvent: HttpAuditEvent = mock[HttpAuditEvent]

    val configuration: Configuration = Configuration(
      "appName" -> "myApp",
      "bootstrap.errorHandler.warnOnly.statusCodes" -> List.empty,
      "bootstrap.errorHandler.suppress4xxErrorMessages" -> false,
      "bootstrap.errorHandler.suppress5xxErrorMessages" -> false
    )

    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

    val handler = new ErrorHandler(configuration, httpAuditEvent, auditConnector)
  }

  "onClientError" should {
    "return 404 with error body" when {
      "header is supplied and message is ERROR_NOT_FOUND" in new Test {
        private val result = handler.onClientError(requestHeader, NOT_FOUND, "test")
        status(result) shouldBe Status.NOT_FOUND

        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(NOT_FOUND, "ERROR_NOT_FOUND"))
      }
    }

    "return 400 with error body" when {
      "header is supplied and message is ERROR_SA_UTR_INVALID" in new Test {
        private val result = handler.onClientError(requestHeader, BAD_REQUEST, "ERROR_SA_UTR_INVALID")
        status(result) shouldBe BAD_REQUEST

        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(BAD_REQUEST, "ERROR_SA_UTR_INVALID"))
      }

      "header is supplied and message is ERROR_TAX_YEAR_INVALID" in new Test {
        private val result = handler.onClientError(requestHeader, BAD_REQUEST, "ERROR_TAX_YEAR_INVALID")
        status(result) shouldBe BAD_REQUEST

        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(BAD_REQUEST, "ERROR_TAX_YEAR_INVALID"))
      }

      "header is supplied and no message is specified" in new Test {
        private val result = handler.onClientError(requestHeader, BAD_REQUEST, "")
        status(result) shouldBe BAD_REQUEST

        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(BAD_REQUEST, "ERROR_BAD_REQUEST"))
      }
    }

    "return 403 with message" when {
      "header is supplied and status code is 403" in new Test {
        private val result = handler.onClientError(requestHeader, FORBIDDEN, "test")
        status(result) shouldBe FORBIDDEN

        contentAsString(result) shouldBe "test"
      }
    }
  }
}
