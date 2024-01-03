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

package uk.gov.hmrc.nationalinsurancedesstub.controllers

import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

trait HeaderValidator extends Results {

  def cc: ControllerComponents

  def validateVersion[T](allowed: Seq[T], version: T): Boolean =
    allowed.contains(version)

  def validateContentType(contentType: String): Boolean =
    contentType == "json"

  private val matchHeader: String => Option[Match] =
    new Regex("""^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$""", "version", "contenttype").findFirstMatchIn(_)

  def acceptHeaderValidationRules(allowed: String*)(headerValue: Option[String]): Option[String] =
    for {
      value      <- headerValue
      res        <- matchHeader(value)
      contentType = res.group("contenttype")
      version     = res.group("version")
      if validateContentType(contentType) && validateVersion(allowed, version)
    } yield version

  def validateAccept(rules: Option[String] => Option[String]): ActionFilter[Request] = new ActionFilter[Request] {
    def filter[A](request: Request[A]): Future[Option[Result]] =
      Future.successful {
        rules(request.headers.get("Accept")) match {
          case Some(_) => None
          case None    =>
            Some(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson[ErrorResponse](ErrorAcceptHeaderInvalid)))
        }
      }

    override protected def executionContext: ExecutionContext = cc.executionContext
  }

  protected def executionContext: ExecutionContext
}
