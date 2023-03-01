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

package uk.gov.hmrc.nationalinsurancedesstub.models

import play.api.libs.json.{JsObject, Json}

class InvalidScenarioException(scenario: String) extends RuntimeException(s"$scenario is not a valid test scenario")

object JsonErrorResponse {
  def apply(code: String, message: String): JsObject = Json.obj("code" -> code, "message" -> message)
}
