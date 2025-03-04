/*
 * copyright 2025 HM Revenue & Customs
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

import org.bson.types.ObjectId
import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoFormats

package object models {
  implicit val formatObjectId: Format[ObjectId]                                  = MongoFormats.objectIdFormat
  implicit val formatCreateSummaryRequest: OFormat[CreateSummaryRequest]         = Json.format[CreateSummaryRequest]
  implicit val class1nicsFmt: OFormat[Class1NICs]                                = Json.format[Class1NICs]
  implicit val class2nicsFmt: OFormat[Class2NICs]                                = Json.format[Class2NICs]
  implicit val nicsFmt: OFormat[NICs]                                            = Json.format[NICs]
  implicit val formatNationalInsuranceSummary: OFormat[NationalInsuranceSummary] = Json.format[NationalInsuranceSummary]
}
