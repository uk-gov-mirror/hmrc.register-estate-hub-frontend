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

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.Request
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.{URI, URLEncoder}


@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig) {

  final val ENGLISH = "en"
  final val WELSH = "cy"
  final val UK_COUNTRY_CODE = "GB"

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = configuration.get[String]("urls.logout")

  lazy val logoutAudit: Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.logout")

  lazy val countdownLength: Int = configuration.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = configuration.get[Int]("timeout.length")

  private lazy val agentsSubscriptionsUrl : String = configuration.get[String]("urls.agentSubscriptions")
  lazy val agentServiceRegistrationUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"
  lazy val registerYourClientsEstateUrl: String = configuration.get[String]("urls.registerYourClientsEstate")

  lazy val estatesUrl: String = servicesConfig.baseUrl("estates")
  lazy val estatesStoreUrl: String = servicesConfig.baseUrl("estates-store") + "/estates-store"

  lazy val locationCanonicalList: String = configuration.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = configuration.get[String]("location.canonical.list.allCY")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  val cacheTtlSeconds: Long = configuration.get[Long]("mongodb.timeToLiveInSeconds")
  val dropIndexes: Boolean = configuration.getOptional[Boolean]("microservice.services.features.mongo.dropIndexes").getOrElse(false)

  lazy val estateDetailsEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.estate-details.enabled")
  lazy val personalRepEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.personal-rep.enabled")
  lazy val deceasedPersonsEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.deceased-persons.enabled")
  lazy val registerTaxEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.register-tax.enabled")

  lazy val maintainAnEstateFrontendUrl : String =
    configuration.get[String]("urls.maintainAnEstate")

  lazy val estateDetailsFrontendUrl: String =
    configuration.get[String]("urls.estateDetails")

  lazy val personalRepFrontendUrl: String =
    configuration.get[String]("urls.personalRep")

  lazy val deceasedPersonsFrontendUrl: String =
    configuration.get[String]("urls.deceasedPersons")

  lazy val registerTaxFrontendUrl: String =
    configuration.get[String]("urls.registerTaxLiability")

  lazy val featureUnavailableUrl: String =
    configuration.get[String]("urls.featureUnavailable")

  lazy val agentDetails: String =
    configuration.get[String]("urls.agentDetails")

  lazy val suitabilityUrl: String =
    configuration.get[String]("urls.suitability")

  lazy val guidanceUrl: String =
    configuration.get[String]("urls.estateGuidance")

  lazy val ttlInSeconds: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  private lazy val accessibilityLinkBaseUrl = configuration.get[String]("urls.accessibility")
  def accessibilityLinkUrl(implicit request: Request[_]): String = {
    val userAction = URLEncoder.encode(new URI(request.uri).getPath, "UTF-8")
    s"$accessibilityLinkBaseUrl?userAction=$userAction"
  }

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case WELSH => "urls.welshHelpline"
      case _ => "urls.estatesHelpline"
    }

    configuration.get[String](path)
  }

}
