/*
 * Copyright 2020 HM Revenue & Customs
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

import org.scalatest.Matchers
import uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders
import uk.gov.hmrc.nationalinsurancedesstub.models.TaxYear
import uk.gov.hmrc.play.test.UnitSpec

class TaxYearBinderSpec extends UnitSpec with Matchers{

  "a valid tax year '2014-15'" should {
    "be transformed to an TaxYear object" in {
      val ty = "2014-15"
      Binders.taxYearBinder.bind("taxYear", ty) shouldBe Right(TaxYear("2014-15"))
    }
  }

  "an invalid tax year 'invalid tax year'" should {
    "be transformed to a String error message" in {
      val ty = "invalid tax year"
      Binders.taxYearBinder.bind("taxYear", ty) shouldBe Left("ERROR_TAX_YEAR_INVALID")
    }
  }

}
