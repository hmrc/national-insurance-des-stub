/*
 * copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.nationalinsurancedesstub

import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc._
import uk.gov.hmrc.nationalinsurancedesstub.controllers._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.backend.http.JsonErrorHandler
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ErrorHandler @Inject() (configuration: Configuration, auditEvent: HttpAuditEvent, auditConnector: AuditConnector)(
  implicit ec: ExecutionContext
) extends JsonErrorHandler(auditConnector, auditEvent, configuration) {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    if (statusCode == play.api.http.Status.BAD_REQUEST) {
      val errorScenario = message match {
        case "ERROR_TAX_YEAR_INVALID" => ErrorTaxYearInvalid
        case "ERROR_SA_UTR_INVALID"   => ErrorSaUtrInvalid
        case _                        => ErrorGenericBadRequest
      }
      Future.successful(Status(ErrorGenericBadRequest.httpStatusCode)(Json.toJson(errorScenario)))
    } else if (statusCode == play.api.http.Status.NOT_FOUND) {
      Future.successful(Status(ErrorNotFound.httpStatusCode)(Json.toJson(ErrorNotFound)))
    } else {
      Future.successful(Status(statusCode)(message))
    }
}
