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

package uk.gov.hmrc.nationalinsurancedesstub.controllers

import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.nationalinsurancedesstub.common.StubResource
import uk.gov.hmrc.nationalinsurancedesstub.models.{NationalInsuranceSummary, TaxYear}
import uk.gov.hmrc.nationalinsurancedesstub.repositories.NationalInsuranceSummaryRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

trait NationalInsuranceSummaryController extends BaseController with StubResource {
  val repository: NationalInsuranceSummaryRepository

  def fetch(utr: String, taxEndYear: String) = Action.async { implicit request =>
    val nationalInsuranceSummaryFuture = repository.fetch(utr, taxEndYear)
    val documentFuture = findResource("/public/national-insurance-summary.json")

    (for {
      nationalInsuranceSummary <- nationalInsuranceSummaryFuture
      document <- documentFuture
    } yield nationalInsuranceSummary match {
      case Some(_) => Ok(Json.parse(document))
      case _ => NotFound
    }) recover {
      case _ => InternalServerError
    }
  }

  def create(saUtr: SaUtr, taxYear: TaxYear) = Action.async { implicit request =>
    repository.store(NationalInsuranceSummary(saUtr.utr, taxYear.endYr)) map {
      _ => Created
    } recover {
      case _ => InternalServerError
    }
  }
}

class NationalInsuranceSummaryControllerImpl extends NationalInsuranceSummaryController {
  override val repository = NationalInsuranceSummaryRepository()
}