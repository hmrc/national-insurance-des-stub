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

package uk.gov.hmrc.nationalinsurancedesstub.repositories

import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}
import uk.gov.hmrc.nationalinsurancedesstub.models.{JsonFormatters, NationalInsuranceSummary}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait NationalInsuranceSummaryRepository extends Repository[NationalInsuranceSummary, BSONObjectID] {
  def store[T <: NationalInsuranceSummary](nationalInsuranceSummary: T): Future[T]
  def fetch(utr: String, taxYear: String): Future[Option[NationalInsuranceSummary]]
}

object NationalInsuranceSummaryRepository extends MongoDbConnection {
  private lazy val repository = new NationalInsuranceSummaryMongoRepository
  def apply(): NationalInsuranceSummaryRepository = repository
}

class NationalInsuranceSummaryMongoRepository(implicit mongo: () => DB)
  extends ReactiveRepository[NationalInsuranceSummary, BSONObjectID]("national-insurance-summary", mongo,
    JsonFormatters.formatNationalInsuranceSummary, JsonFormatters.formatObjectId)
  with NationalInsuranceSummaryRepository {

  override def store[T <: NationalInsuranceSummary](nationalInsuranceSummary: T): Future[T] = {
    insert(nationalInsuranceSummary) map {_ => nationalInsuranceSummary}
  }

  def fetch(utr: String, taxYear: String): Future[Option[NationalInsuranceSummary]] = {
    find("utr" -> utr, "taxYear" -> taxYear) map (_.headOption)
  }
}
