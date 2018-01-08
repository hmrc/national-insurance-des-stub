/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.nationalinsurancedesstub

import play.api.libs.json.Json
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

package object models {
  implicit val formatObjectId = ReactiveMongoFormats.objectIdFormats
  implicit val formatCreateSummaryRequest = Json.format[CreateSummaryRequest]
  implicit val class1nicsFmt = Json.format[Class1NICs]
  implicit val class2nicsFmt = Json.format[Class2NICs]
  implicit val nicsFmt = Json.format[NICs]
  implicit val formatNationalInsuranceSummary = Json.format[NationalInsuranceSummary]

  implicit val apiAccessFmt = Json.format[APIAccess]

}
