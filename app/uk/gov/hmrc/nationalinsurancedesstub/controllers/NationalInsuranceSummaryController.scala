/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.mvc._
import uk.gov.hmrc.api.controllers.HeaderValidator
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.nationalinsurancedesstub.models._
import uk.gov.hmrc.nationalinsurancedesstub.services.{NationalInsuranceSummaryService, ScenarioLoader}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class NationalInsuranceSummaryController @Inject()(val scenarioLoader: ScenarioLoader, val service: NationalInsuranceSummaryService)
  extends BaseController with HeaderValidator {

  def fetch(utr: String, taxEndYear: String) = Action.async {
    service.fetch(utr, taxEndYear) map {
      case Some(result) => Ok(Json.toJson(result.nics))
      case _ => NotFound
    } recover {
      case _ => InternalServerError
    }
  }

  def create(saUtr: SaUtr, taxYear: TaxYear) = validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
    withJsonBody[CreateSummaryRequest] { createSummaryRequest =>
      val scenario = createSummaryRequest.scenario.getOrElse("HAPPY_PATH_1")

      for {
        nics <- scenarioLoader.loadScenario(scenario)
        _ <- service.create(saUtr.utr, taxYear.endYr, nics)
      } yield Created(Json.toJson(nics))

    } recover {
      case _: InvalidScenarioException => BadRequest(ErrorResponse("UNKNOWN_SCENARIO", "Unknown test scenario"))
      case _ => InternalServerError
    }
  }
}
