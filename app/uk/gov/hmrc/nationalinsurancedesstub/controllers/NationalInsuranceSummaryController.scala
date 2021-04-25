/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, BodyParser, ControllerComponents}
import uk.gov.hmrc.api.controllers.HeaderValidator
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.nationalinsurancedesstub.models._
import uk.gov.hmrc.nationalinsurancedesstub.services.{NationalInsuranceSummaryService, ScenarioLoader}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class NationalInsuranceSummaryController @Inject()(val scenarioLoader: ScenarioLoader,
    val service: NationalInsuranceSummaryService,
    val cc: ControllerComponents)
  extends BackendController(cc) with HeaderValidator {

  implicit val executionContext: ExecutionContext = cc.executionContext
  val parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

  def fetch(utr: String, taxEndYear: String): Action[AnyContent] = Action.async {
    service.fetch(utr, taxEndYear) map {
      case Some(result) => Ok(Json.toJson(result.nics))
      case _ => NotFound
    } recover {
      case _ => InternalServerError
    }
  }

  def create(saUtr: SaUtr, taxYear: TaxYear): Action[JsValue] = validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
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
