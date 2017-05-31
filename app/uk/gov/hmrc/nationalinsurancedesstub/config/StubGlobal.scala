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

package uk.gov.hmrc.nationalinsurancedesstub.config

import javax.inject.{Inject, Singleton}

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.http.DefaultHttpFilters
import uk.gov.hmrc.play.config.ControllerConfig
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.http.logging.filters.LoggingFilter


@Singleton
class ControllerConfiguration @Inject()(configuration: Configuration) extends ControllerConfig {
  lazy val controllerConfigs = configuration.underlying.as[Config]("controllers")
}

@Singleton
class MicroserviceLoggingFilter @Inject()(configuration: ControllerConfiguration)
  extends LoggingFilter with MicroserviceFilterSupport {

  override def controllerNeedsLogging(controllerName: String) = configuration.paramsForController(controllerName).needsLogging
}

@Singleton
class Filters @Inject() (loggingFilter: MicroserviceLoggingFilter) extends DefaultHttpFilters (loggingFilter)
