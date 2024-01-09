/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.nationalinsurancedesstub.controllers

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.nationalinsurancedesstub.models._
import uk.gov.hmrc.nationalinsurancedesstub.services.{NationalInsuranceSummaryService, ScenarioLoader}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class NationalInsuranceSummaryController @Inject()(
                                                    val scenarioLoader: ScenarioLoader,
                                                    val service: NationalInsuranceSummaryService,
                                                    val cc: ControllerComponents
                                                  ) extends BackendController(cc)
  with HeaderValidator {

  implicit val executionContext: ExecutionContext = cc.executionContext

  def fetch(utr: String, taxEndYear: String): Action[AnyContent] = Action.async {
    service.fetch(utr, taxEndYear) map {
      case Some(result) => Ok(Json.toJson(result.nics))
      case _ => NotFound
    } recover { case _ =>
      InternalServerError
    }
  }

  def create(saUtr: SaUtr, taxYear: TaxYear): Action[JsValue] =
    (cc.actionBuilder andThen validateAccept(acceptHeaderValidationRules("1.0", "1.1"))).async(parse.json) {
      implicit request =>
        withJsonBody[CreateSummaryRequest] { createSummaryRequest =>
          val scenario = createSummaryRequest.scenario.getOrElse("HAPPY_PATH_1")

          for {
            nics <- scenarioLoader.loadScenario(scenario)
            _ <- service.create(saUtr.utr, taxYear.endYr, nics)
          } yield Created(Json.toJson(nics))

        } recover {
          case _: InvalidScenarioException => BadRequest(JsonErrorResponse("UNKNOWN_SCENARIO", "Unknown test scenario"))
          case _ => InternalServerError
        }
    }
}
