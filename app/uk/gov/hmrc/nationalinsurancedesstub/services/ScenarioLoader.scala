/*
 * Copyright 2024 HM Revenue & Customs
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

import javax.inject.Singleton

import play.api.libs.json.Json
import uk.gov.hmrc.nationalinsurancedesstub.models._

import scala.concurrent.Future

@Singleton
class ScenarioLoader {

  private def pathForScenario(scenario: String) =
    s"/public/scenarios/$scenario.json"

  def loadScenario(scenario: String): Future[NICs] = {
    val resource = getClass.getResourceAsStream(pathForScenario(scenario))
    if (resource == null) {
      Future.failed(new InvalidScenarioException(scenario))
    } else {
      Future.successful(Json.parse(resource).as[NICs])
    }
  }
}
