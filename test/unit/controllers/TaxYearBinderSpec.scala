/*
 * Copyright 2023 HM Revenue & Customs
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

package unit.controllers

import uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders
import uk.gov.hmrc.nationalinsurancedesstub.models.TaxYear
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TaxYearBinderSpec extends AnyWordSpec with Matchers {

  "a valid tax year '2014-15'" should {
    "be transformed to an TaxYear object" in {
      val ty = "2014-15"
      Binders.taxYearBinder.bind("taxYear", ty) shouldBe Right(TaxYear("2014-15"))
    }
  }

  "unbinding a TaxYear object" should {
    "result in a taxYear" in {
      val taxYear = "2020-21"
      Binders.taxYearBinder.unbind("taxYear", TaxYear(taxYear)) shouldBe taxYear
    }
  }

  "an invalid tax year 'invalid tax year'" should {
    "be transformed to a String error message" in {
      val ty = "invalid tax year"
      Binders.taxYearBinder.bind("taxYear", ty) shouldBe Left("ERROR_TAX_YEAR_INVALID")
    }
  }

}
