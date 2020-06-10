/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "estates"

  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val authUrl: String = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = configuration.get[String]("urls.logout")

  lazy val estatesUrl: String = configuration.get[Service]("microservice.services.estates").baseUrl
  lazy val estatesStoreUrl: String = configuration.get[Service]("microservice.services.estates-store").baseUrl + "/estates-store"

  lazy val locationCanonicalList: String = configuration.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListNonUK: String = configuration.get[String]("location.canonical.list.nonUK")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val estateDetailsEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.estate-details.enabled")
  lazy val personalRepEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.personal-rep.enabled")
  lazy val deceasedPersonsEnabled: Boolean = configuration.get[Boolean]("microservice.services.features.deceased-persons.enabled")

  lazy val estateDetailsFrontendUrl: String =
    configuration.get[String]("urls.estateDetails")

  lazy val personalRepFrontendUrl: String =
    configuration.get[String]("urls.personalRep")

  lazy val deceasedPersonsFrontendUrl: String =
    configuration.get[String]("urls.deceasedPersons")

  lazy val featureUnavailableUrl: String =
    configuration.get[String]("urls.featureUnavailable")

  lazy val ttlInSeconds: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")
}
