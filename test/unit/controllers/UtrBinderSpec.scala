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

package unit.controllers

import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UtrBinderSpec extends AnyWordSpec with Matchers {

  "a valid utr '1097172564'" should {
    "be transformed to an SaUtr object" in {
      val utr = "1097172564"
      Binders.saUtrBinder.bind("utr",utr) shouldBe Right(SaUtr(utr))
    }
  }

  "an invalid utr 'invalid'" should {
    "be transformed to a String error message" in {
      val utr = "invalid"
      Binders.saUtrBinder.bind("utr",utr) shouldBe Left("ERROR_SA_UTR_INVALID")
    }
  }

}
