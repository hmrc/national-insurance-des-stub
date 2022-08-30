/*
 * Copyright 2022 HM Revenue & Customs
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

package unit.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.nationalinsurancedesstub.models.TaxYear

class TaxYearSpec extends AnyWordSpec with Matchers {

  private val validTaxYears   = Seq("2019-20", "2020-21", "2021-22", "2022-23", "2023-24")
  private val invalidTaxYears = Seq("2019", "201920", "2019-19", "2019-21", "2019-1P")

  "isValid" should {
    validTaxYears.foreach { taxYear =>
      s"return true for valid tax year $taxYear" in {
        TaxYear.isValid(taxYear) shouldBe true
      }
    }

    invalidTaxYears.foreach { taxYear =>
      s"return false for invalid tax year $taxYear" in {
        TaxYear.isValid(taxYear) shouldBe false
      }
    }
  }

  "TaxYear constructor" should {
    validTaxYears.foreach { taxYear =>
      s"create a taxYear for a valid argument $taxYear" in {
        TaxYear(taxYear).ty shouldBe taxYear
      }
    }

    invalidTaxYears.foreach { taxYear =>
      s"throw an IllegalArgumentException for an invalid argument $taxYear" in {
        assertThrows[IllegalArgumentException](
          TaxYear(taxYear)
        )
      }
    }
  }
}
