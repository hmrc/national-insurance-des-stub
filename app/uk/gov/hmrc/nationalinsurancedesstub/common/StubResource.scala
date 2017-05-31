/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.nationalinsurancedesstub.common

import java.io.InputStream

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Results

import scala.io.Source

trait StubResource extends Results {

  def resourceAsResponse(path: String) = {
    findResource(path) match {
      case Some(content) => Ok(Json.parse(content))
      case _ => NotFound
    }
  }

  def findResource(path: String): Option[String] = {
    val resource = getClass.getResourceAsStream(path)
    if (resource == null) {
      Logger.warn(s"Could not find resource '$path'")
      None
    } else {
      Some(readStreamToString(resource))
    }
  }

  private def readStreamToString(is: InputStream) = {
    try Source.fromInputStream(is).mkString.toString
    finally is.close()
  }

}
