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

import play.api.Configuration
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.nationalinsurancedesstub.models.APIAccess

class APIAccessSpec extends AnyWordSpec with Matchers {

  private trait Test {

    val version: String = "1.0"

    val configuration: Configuration = Configuration.from(
      Map(
        s"version-$version.type"                      -> "PUBLIC",
        s"version-$version.whitelistedApplicationIds" -> Seq(
          "a1e8057e-fbbc-47a8-a8b4-78d9f015c253",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
        )
      )
    )

    val apiAccess: APIAccess = APIAccess.build(Option(configuration))(version)
  }

  "build" when {

    "configuration options are specified" should {

      "return the configured type" in new Test {
        apiAccess.`type` shouldBe "PUBLIC"
      }

      "return the configured whitelistedApplicationIds" in new Test {
        apiAccess.whitelistedApplicationIds shouldBe
          Some(Seq("a1e8057e-fbbc-47a8-a8b4-78d9f015c253", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"))
      }
    }

    "configuration options are not specified" should {

      "default type to PRIVATE" in new Test {
        override val configuration: Configuration = Configuration.empty

        apiAccess.`type` shouldBe "PRIVATE"
      }

      "default whitelistedApplicationIds to None" in new Test {
        override val configuration: Configuration = Configuration.empty

        apiAccess.whitelistedApplicationIds shouldBe None
      }
    }
  }
}
