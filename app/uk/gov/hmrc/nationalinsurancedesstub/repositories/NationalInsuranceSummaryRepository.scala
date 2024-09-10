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

package uk.gov.hmrc.nationalinsurancedesstub.repositories

import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.nationalinsurancedesstub.models._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.FindOneAndReplaceOptions

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import org.mongodb.scala.SingleObservableFuture

@Singleton
class NationalInsuranceSummaryRepository @Inject() (mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[NationalInsuranceSummary](
      mongoComponent = mongo,
      collectionName = "national-insurance-summary",
      domainFormat = formatNationalInsuranceSummary,
      indexes = Seq.empty
    ) {

  def store(nationalInsuranceSummary: NationalInsuranceSummary): Future[NationalInsuranceSummary] =
    collection
      .findOneAndReplace(
        and(equal("utr", nationalInsuranceSummary.utr), equal("taxYear", nationalInsuranceSummary.taxYear)),
        nationalInsuranceSummary,
        FindOneAndReplaceOptions().upsert(true)
      )
      .toFuture()

  def fetch(utr: String, taxYear: String): Future[Option[NationalInsuranceSummary]] =
    collection.find(and(equal("utr", utr), equal("taxYear", taxYear))).headOption()
}
