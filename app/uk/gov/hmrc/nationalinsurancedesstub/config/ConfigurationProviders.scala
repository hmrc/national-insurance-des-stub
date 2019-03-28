/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.{Inject, Provider, Singleton}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.nationalinsurancedesstub.connectors.ServiceLocatorConfig
import uk.gov.hmrc.play.config.ServicesConfig

class ConfigurationModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[ServiceLocatorRegistrationConfig].toProvider[ServiceLocatorRegistrationConfigProvider],
      bind[ServiceLocatorConfig].toProvider[ServiceLocatorConfigProvider]
    )
  }
}

@Singleton
class ServiceLocatorRegistrationConfigProvider @Inject()(val runModeConfiguration: Configuration, environment: Environment)
  extends Provider[ServiceLocatorRegistrationConfig] with ServicesConfig {

  override protected def mode = environment.mode

  override def get() = {
    val registrationEnabled = getConfBool("service-locator.enabled", defBool = true)
    ServiceLocatorRegistrationConfig(registrationEnabled)
  }
}

@Singleton
class ServiceLocatorConfigProvider @Inject()(val runModeConfiguration: Configuration, environment: Environment)
  extends Provider[ServiceLocatorConfig] with ServicesConfig {

  override protected def mode = environment.mode

  override def get() = {
    val appName = getString("appName")
    val appUrl = getString("appUrl")
    val serviceLocatorBaseUrl = baseUrl("service-locator")
    ServiceLocatorConfig(appName, appUrl, serviceLocatorBaseUrl)
  }
}



