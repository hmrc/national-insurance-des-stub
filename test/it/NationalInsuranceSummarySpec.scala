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

package it

import it.helpers.BaseSpec
import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import uk.gov.hmrc.mongo.MongoConnector
import uk.gov.hmrc.nationalinsurancedesstub.repositories.NationalInsuranceSummaryMongoRepository

import scala.concurrent.Await.result
import scala.concurrent.ExecutionContext.Implicits.global
import scalaj.http.Http

class NationalInsuranceSummarySpec extends BaseSpec {
  feature("Fetch National Insurance summary") {
    scenario("No data is returned because UTR and tax year are not found") {
      When("I request a National Insurance summary for a given UTR and tax year")
      val response = fetchNationalInsuranceSummary("2234567890", "2015")

      Then("The response should indicate that no data was found")
      response.code shouldBe NOT_FOUND
    }

    scenario("National Insurance summary is returned for given UTR and tax year") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary("2234567890", "2014-15")

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary("2234567890", "2015")

      Then("The response should contain the National Insurance summary")
      response.code shouldBe OK
    }

  }

  private def primeNationalInsuranceSummary(utr: String, taxYear: String) =
    postEndpoint(s"national-insurance-test-support/sa/$utr/annual-summary/$taxYear")

  private def fetchNationalInsuranceSummary(utr: String, taxYearEnd: String) =
    getEndpoint(s"nics/utr/$utr/year/$taxYearEnd/summary")

  private def getEndpoint(endpoint: String) =
    Http(s"$serviceUrl/$endpoint").method("GET").asString

  private def postEndpoint(endpoint: String) =
    Http(s"$serviceUrl/$endpoint").method("POST").asString

  override protected def beforeEach(): Unit = {
    result(mongoRepository.drop, timeout)
    result(mongoRepository.ensureIndexes, timeout)
  }

  def mongoRepository = {
    implicit val mongo = MongoConnector(mongoUri).db
    new NationalInsuranceSummaryMongoRepository()
  }
}
