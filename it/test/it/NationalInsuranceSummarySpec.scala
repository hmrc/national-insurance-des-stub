/*
 * Copyright 2025 HM Revenue & Customs
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

import it.helpers.{BaseSpec, HttpClient, ResourceLoader}
import play.api.http.HeaderNames
import play.api.http.Status.{BAD_REQUEST, CREATED, NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}
import uk.gov.hmrc.nationalinsurancedesstub.repositories.NationalInsuranceSummaryRepository
import org.mongodb.scala.SingleObservableFuture

import scala.concurrent.Await.result

class NationalInsuranceSummarySpec extends BaseSpec with HttpClient with ResourceLoader {

  private val validUtr   = "2234567890"
  private val invalidUtr = "INVALID"

  private val validTaxYear    = "2014-15"
  private val invalidTaxYear  = "15"
  private val validTaxYearEnd = "2015"

  private val emptyPayload           = "{}"
  private val happyPath1Payload      = """{ "scenario": "HAPPY_PATH_1" }"""
  private val happyPath2Payload      = """{ "scenario": "HAPPY_PATH_2" }"""
  private val invalidScenarioPayload = """{ "scenario": "NON_EXISTENT" }"""

  private val happyPath1FilePath = "/public/scenarios/HAPPY_PATH_1.json"
  private val happyPath2FilePath = "/public/scenarios/HAPPY_PATH_2.json"

  Feature("Prime National Insurance summary") {

    Scenario("400 BAD REQUEST is returned when UTR is invalid") {
      When("I attempt to prime a National Insurance summary for an invalid UTR and valid tax year")
      val response = primeNationalInsuranceSummary(invalidUtr, validTaxYear, emptyPayload)

      Then("The response status should be BAD REQUEST")
      response.status shouldBe BAD_REQUEST

      And("The statusCode should be 400")
      (Json.parse(response.body) \ "statusCode").as[Int] shouldBe BAD_REQUEST

      And("The error message should be ERROR_SA_UTR_INVALID")
      (Json.parse(response.body) \ "message").as[String] shouldBe "ERROR_SA_UTR_INVALID"
    }

    Scenario("400 BAD REQUEST is returned when tax year is invalid") {
      When("I attempt to prime a National Insurance summary for a valid UTR and invalid tax year")
      val response = primeNationalInsuranceSummary(validUtr, invalidTaxYear, emptyPayload)

      Then("The response status should be BAD REQUEST")
      response.status shouldBe BAD_REQUEST

      And("The statusCode should be 400")
      (Json.parse(response.body) \ "statusCode").as[Int] shouldBe BAD_REQUEST

      And("The error message should be ERROR_TAX_YEAR_INVALID")
      (Json.parse(response.body) \ "message").as[String] shouldBe "ERROR_TAX_YEAR_INVALID"
    }

    Scenario("National Insurance summary can be primed") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath2Payload)

      Then("The response should indicate that the summary has been created")
      primeResponse.status shouldBe CREATED
    }

    Scenario("National Insurance summary cannot be primed with an invalid scenario") {
      When("I attempt to prime a National Insurance summary with an invalid scenario")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, invalidScenarioPayload)

      Then("The response code should be BAD_REQUEST")
      primeResponse.status shouldBe BAD_REQUEST

      And("The error code should be UNKNOWN_SCENARIO")
      (Json.parse(primeResponse.body) \ "code").as[String] shouldBe "UNKNOWN_SCENARIO"
    }

  }

  Feature("Fetch National Insurance summary") {

    Scenario("No data is returned because UTR and tax year are not found") {
      When("I request a National Insurance summary for a given UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response should indicate that no data was found")
      response.status shouldBe NOT_FOUND
    }

    Scenario("National Insurance summary is returned when primed with specific scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath2Payload)

      Then("The response should indicate that the summary has been created")
      primeResponse.status shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response code should be OK")
      response.status shouldBe OK

      And("The response should contain the National Insurance summary")
      val expected = loadResource(happyPath2FilePath)
      Json.parse(response.body) shouldBe Json.parse(expected)
    }

    Scenario("Happy Path 1 National Insurance summary is returned when primed with default scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, emptyPayload)

      Then("The response should indicate that the summary has been created")
      primeResponse.status shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response code should be OK")
      response.status shouldBe OK

      And("The response should contain the National Insurance summary")
      val expected = loadResource(happyPath1FilePath)
      Json.parse(response.body) shouldBe Json.parse(expected)
    }

    Scenario("National Insurance summary can be re-primed with a different scenario") {
      When("I prime a National Insurance summary for a given UTR and tax year")
      val initialPrimeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath1Payload)

      Then("The response should indicate that the summary has been created")
      initialPrimeResponse.status shouldBe CREATED

      And("I re-prime the National Insurance summary for the same UTR and tax year with a different scenario")
      val primeResponse = primeNationalInsuranceSummary(validUtr, validTaxYear, happyPath2Payload)

      Then("The response should indicate that the summary has been created")
      primeResponse.status shouldBe CREATED

      And("I request a National Insurance summary for the same UTR and tax year")
      val response = fetchNationalInsuranceSummary(validUtr, validTaxYearEnd)

      Then("The response code should be OK")
      response.status shouldBe OK

      And("The response should contain the National Insurance summary for the changed scenario")
      val expected = loadResource(happyPath2FilePath)
      Json.parse(response.body) shouldBe Json.parse(expected)
    }
  }

  private def primeNationalInsuranceSummary(
    utr: String,
    taxYear: String,
    payload: String
  ): StandaloneWSResponse =
    result(
      awaitable = post(
        url = s"$serviceUrl/sa/$utr/annual-summary/$taxYear",
        requestBody = payload,
        headers = Seq(
          (HeaderNames.CONTENT_TYPE, "application/json"),
          (HeaderNames.ACCEPT, "application/vnd.hmrc.1.0+json")
        )
      ),
      atMost = timeout
    )

  private def fetchNationalInsuranceSummary(utr: String, taxYearEnd: String): StandaloneWSRequest#Response =
    result(
      awaitable = get(url = s"$serviceUrl/nics/utr/$utr/year/$taxYearEnd/summary"),
      atMost = timeout
    )

  override protected def beforeEach(): Unit = {
    val repository = app.injector.instanceOf[NationalInsuranceSummaryRepository]
    result(repository.collection.drop().toFuture(), timeout)
    result(repository.ensureIndexes(), timeout)
  }
}
