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

package uk.gov.hmrc.nationalinsurancedesstub.repositories

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.nationalinsurancedesstub.models._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NationalInsuranceSummaryRepository @Inject()(mongo: ReactiveMongoComponent)(implicit ec: ExecutionContext)
  extends ReactiveRepository[NationalInsuranceSummary, BSONObjectID]("national-insurance-summary", mongo.mongoConnector.db,
    formatNationalInsuranceSummary, formatObjectId) {

  def store[T <: NationalInsuranceSummary](nationalInsuranceSummary: T): Future[T] = {
    findAndUpdate(
      Json.obj("utr" -> nationalInsuranceSummary.utr, "taxYear" -> nationalInsuranceSummary.taxYear),
      Json.toJson(nationalInsuranceSummary).as[JsObject],
      fetchNewObject = false,
      upsert = true).map(_ => nationalInsuranceSummary)
  }

  def fetch(utr: String, taxYear: String): Future[Option[NationalInsuranceSummary]] = {
    find("utr" -> utr, "taxYear" -> taxYear) map (_.headOption)
  }
}
