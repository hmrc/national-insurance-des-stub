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

package it.helpers

import org.apache.pekko.actor.ActorSystem
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Future
import play.api.libs.ws.WSBodyWritables.*

trait HttpClient {

  implicit val actorSystem: ActorSystem = ActorSystem()

  private val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()

  def get(url: String): Future[StandaloneWSRequest#Response] =
    wsClient
      .url(url)
      .get()

  def post(
    url: String,
    requestBody: String,
    headers: Seq[(String, String)]
  ): Future[StandaloneWSResponse] =
    wsClient
      .url(url)
      .withHttpHeaders(headers*)
      .post(requestBody)
}
