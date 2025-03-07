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

package uk.gov.hmrc.nationalinsurancedesstub.services

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.nationalinsurancedesstub.models.{NICs, NationalInsuranceSummary}
import uk.gov.hmrc.nationalinsurancedesstub.repositories.NationalInsuranceSummaryRepository

import scala.concurrent.Future

@Singleton
class NationalInsuranceSummaryService @Inject() (val repository: NationalInsuranceSummaryRepository) {

  def create(utr: String, taxYearEnd: String, nics: NICs): Future[NationalInsuranceSummary] =
    repository.store(NationalInsuranceSummary(utr, taxYearEnd, nics))

  def fetch(utr: String, taxYearEnd: String): Future[Option[NationalInsuranceSummary]] =
    repository.fetch(utr, taxYearEnd)
}
