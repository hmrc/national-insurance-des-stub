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

import common.util.ResourceLoader._
import it.helpers.BaseSpec
import play.api.http.HeaderNames
import play.api.http.Status.{CREATED, NOT_FOUND, OK, BAD_REQUEST}
import play.api.libs.json.Json
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

    scenario("National Insurance summary can be primed") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary("2234567890", "2014-15", """{ "scenario": "HAPPY_PATH_2" }""")

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED
    }


    scenario("National Insurance summary is returned when primed with specific scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary("2234567890", "2014-15", """{ "scenario": "HAPPY_PATH_2" }""")

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary("2234567890", "2015")

      Then("The response code should be OK")
      response.code shouldBe OK

      And("The response should contain the National Insurance summary")
      val expected = loadResource("/public/scenarios/HAPPY_PATH_2.json")
      Json.parse(response.body) shouldBe Json.parse(expected)
    }

    scenario("Happy Path 1 National Insurance summary is returned when primed with default scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary("2234567890", "2014-15", "{}")

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary("2234567890", "2015")

      Then("The response code should be OK")
      response.code shouldBe OK

      And("The response should contain the National Insurance summary")
      val expected = loadResource("/public/scenarios/HAPPY_PATH_1.json")
      Json.parse(response.body) shouldBe Json.parse(expected)
    }

    scenario("National Insurance summary cannot be primed with an invalid scenario") {
      When("I prime a National Insurance summary with an invalid scenario")
      val primeResponse = primeNationalInsuranceSummary("2234567890", "2014-15", """{ "scenario": "NON_EXISTENT" }""")

      Then("The response code should be BAD_REQUEST")
      primeResponse.code shouldBe BAD_REQUEST

      And("The error code should be UNKNOWN_SCENARIO")
      (Json.parse(primeResponse.body) \ "code").as[String] shouldBe "UNKNOWN_SCENARIO"
    }

  }

  private def primeNationalInsuranceSummary(utr: String, taxYear: String, payload: String) =
    postEndpoint(s"sa/$utr/annual-summary/$taxYear", payload)

  private def fetchNationalInsuranceSummary(utr: String, taxYearEnd: String) =
    getEndpoint(s"nics/utr/$utr/year/$taxYearEnd/summary")

  private def getEndpoint(endpoint: String) =
    Http(s"$serviceUrl/$endpoint").method("GET").asString

  private def postEndpoint(endpoint: String, payload: String) =
    Http(s"$serviceUrl/$endpoint")
      .header(HeaderNames.CONTENT_TYPE, "application/json")
      .method("POST")
      .postData(payload)
      .asString

  override protected def beforeEach(): Unit = {
    result(mongoRepository.drop, timeout)
    result(mongoRepository.ensureIndexes, timeout)
  }

  def mongoRepository = {
    implicit val mongo = MongoConnector(mongoUri).db
    new NationalInsuranceSummaryMongoRepository()
  }
}
