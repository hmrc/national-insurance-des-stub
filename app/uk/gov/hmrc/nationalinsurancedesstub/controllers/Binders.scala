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

package uk.gov.hmrc.nationalinsurancedesstub.controllers

import play.api.mvc.PathBindable
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.nationalinsurancedesstub.models.TaxYear
import uk.gov.hmrc.referencechecker.SelfAssessmentReferenceChecker

object Binders {

  implicit def saUtrBinder(implicit stringBinder: PathBindable[String]): PathBindable[SaUtr] = new PathBindable[SaUtr] {

    def unbind(key: String, saUtr: SaUtr): String = stringBinder.unbind(key, saUtr.value)

    def bind(key: String, value: String): Either[String, SaUtr] =
      if (SelfAssessmentReferenceChecker.isValid(value)) {
        Right(SaUtr(value))
      } else {
        Left("ERROR_SA_UTR_INVALID")
      }
  }

  implicit def taxYearBinder(implicit stringBinder: PathBindable[String]): PathBindable[TaxYear] =
    new PathBindable[TaxYear] {

      override def unbind(key: String, taxYear: TaxYear): String = stringBinder.unbind(key, taxYear.ty)

      override def bind(key: String, value: String): Either[String, TaxYear] =
        if (TaxYear.isValid(value)) {
          Right(TaxYear(value))
        } else {
          Left("ERROR_TAX_YEAR_INVALID")
        }
    }
}
