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

package it

import common.util.ResourceLoader._
import it.helpers.BaseSpec
import play.api.http.HeaderNames
import play.api.http.Status.{BAD_REQUEST, CREATED, NOT_FOUND, OK}
import play.api.libs.json.Json
import scalaj.http.Http
import uk.gov.hmrc.mongo.MongoSpecSupport
import uk.gov.hmrc.nationalinsurancedesstub.repositories.NationalInsuranceSummaryRepository

import scala.concurrent.Await.result
import scala.concurrent.ExecutionContext.Implicits.global

class NationalInsuranceSummarySpec extends BaseSpec with MongoSpecSupport {

  private val validUtr = "2234567890"
  private val invalidUtr = "INVALID"

  private val validTaxYear = "2014-15"
  private val invalidTaxYear = "15"
  private val validTaxYearEnd = "2015"

  private val emptyPayload = "{}"
  private val happyPath1Payload = """{ "scenario": "HAPPY_PATH_1" }"""
  private val happyPath2Payload = """{ "scenario": "HAPPY_PATH_2" }"""
  private val invalidScenarioPayload = """{ "scenario": "NON_EXISTENT" }"""

  val happyPath1FilePath = "/public/scenarios/HAPPY_PATH_1.json"
  val happyPath2FilePath = "/public/scenarios/HAPPY_PATH_2.json"

  Feature("Prime National Insurance summary") {

    Scenario("400 BAD REQUEST is returned when UTR is invalid") {
      When("I attempt to prime a National Insurance summary for an invalid UTR and valid tax year")
      val response = primeNationalInsuranceSummary(invalidUtr, validTaxYear, emptyPayload)

      Then("The response status should be BAD REQUEST")
      response.code shouldBe BAD_REQUEST

      And("The statusCode should be 400")
      (Json.parse(response.body) \ "statusCode").as[Int] shouldBe BAD_REQUEST

      And("The error message should be ERROR_SA_UTR_INVALID")
      (Json.parse(response.body) \ "message").as[String] shouldBe "ERROR_SA_UTR_INVALID"
    }

    Scenario("400 BAD REQUEST is returned when tax year is invalid") {
      When("I attempt to prime a National Insurance summary for a valid UTR and invalid tax year")
      val response = primeNationalInsuranceSummary(validUtr, invalidTaxYear, emptyPayload)

      Then("The response status should be BAD REQUEST")
      response.code shouldBe BAD_REQUEST

      And("The statusCode should be 400")
      (Json.parse(response.body) \ "statusCode").as[Int] shouldBe BAD_REQUEST

      And("The error message should be ERROR_TAX_YEAR_INVALID")
      (Json.parse(response.body) \ "message").as[String] shouldBe "ERROR_TAX_YEAR_INVALID"
    }

    Scenario("National Insurance summary can be primed") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath2Payload)

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED
    }

    Scenario("National Insurance summary cannot be primed with an invalid scenario") {
      When("I attempt to prime a National Insurance summary with an invalid scenario")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, invalidScenarioPayload)

      Then("The response code should be BAD_REQUEST")
      primeResponse.code shouldBe BAD_REQUEST

      And("The error code should be UNKNOWN_SCENARIO")
      (Json.parse(primeResponse.body) \ "code").as[String] shouldBe "UNKNOWN_SCENARIO"
    }

  }

  Feature("Fetch National Insurance summary") {

    Scenario("No data is returned because UTR and tax year are not found") {
      When("I request a National Insurance summary for a given UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response should indicate that no data was found")
      response.code shouldBe NOT_FOUND
    }

    Scenario("National Insurance summary is returned when primed with specific scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath2Payload)

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response code should be OK")
      response.code shouldBe OK

      And("The response should contain the National Insurance summary")
      val expected = loadResource(happyPath2FilePath)
      Json.parse(response.body) shouldBe Json.parse(expected)
    }

    Scenario("Happy Path 1 National Insurance summary is returned when primed with default scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, emptyPayload)

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response code should be OK")
      response.code shouldBe OK

      And("The response should contain the National Insurance summary")
      val expected = loadResource(happyPath1FilePath)
      Json.parse(response.body) shouldBe Json.parse(expected)
    }

    Scenario("National Insurance summary can be re-primed with a different scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val initialPrimeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath1Payload)

      Then("The response should indicate that the summary has been created")
      initialPrimeResponse.code shouldBe CREATED

      And("I re-prime the National Insurance summary for the same UTR and tax year with a different scenario")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath2Payload)

      Then("The response should indicate that the summary has been created")
      primeResponse.code shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response code should be OK")
      response.code shouldBe OK

      And("The response should contain the National Insurance summary for the changed scenario")
      val expected = loadResource(happyPath2FilePath)
      Json.parse(response.body) shouldBe Json.parse(expected)
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
      .header(HeaderNames.ACCEPT, "application/vnd.hmrc.1.0+json")
      .method("POST")
      .postData(payload)
      .asString

  override protected def beforeEach(): Unit = {
    val repository = app.injector.instanceOf[NationalInsuranceSummaryRepository]
    result(repository.drop, timeout)
    result(repository.ensureIndexes, timeout)
  }
}
