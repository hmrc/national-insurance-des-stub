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

package config

import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.nationalinsurancedesstub.config.AppConfig
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends AnyWordSpec with Matchers {

  private val apiStatuses: Seq[String]           = Seq("ALPHA", "BETA", "STABLE", "DEPRECATED", "RETIRED")
  private val mockServicesConfig: ServicesConfig = Mockito.mock(classOf[ServicesConfig])

  private class Setup(apiStatus: String) {
    when(mockServicesConfig.getString("api.status")).thenReturn(apiStatus)

    val appConfig: AppConfig = new AppConfig(config = mockServicesConfig)
  }

  "AppConfig" should {
    apiStatuses.foreach { apiStatus =>
      s"return the api status $apiStatus when set" in new Setup(apiStatus) {
        appConfig.apiStatus shouldBe apiStatus
      }
    }
  }
}
