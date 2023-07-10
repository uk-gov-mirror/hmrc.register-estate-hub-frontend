/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models._
import models.http.DeclarationResponse
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val registerUrl = s"${config.estatesUrl}/estates/register"

  def register(payload: Declaration)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[DeclarationResponse] = {
    http.POST[Declaration, DeclarationResponse](registerUrl, payload)(
      implicitly[Writes[Declaration]], DeclarationResponse.httpReads, hc, ec
    )
  }

  private val getPersonalRepIndUrl = s"${config.estatesUrl}/estates/personal-rep/individual"
  private val getPersonalRepOrgUrl = s"${config.estatesUrl}/estates/personal-rep/organisation"

  def getPersonalRepName()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PersonalRepName] = {
    for {
      individual <- http.GET[Option[PersonalRepName]](getPersonalRepIndUrl)(PersonalRepName.personalRepIndividualNameReads, hc, ec)
      organisation <- http.GET[Option[PersonalRepName]](getPersonalRepOrgUrl)(PersonalRepName.personalRepOrganisationNameReads, hc, ec)
    } yield {
      (individual, organisation) match {
        case (Some(name), None) => name
        case (None, Some(name)) => name
        case _ => throw new RuntimeException("Unable to get personal representatives name")
      }
    }
  }

  private val getEstateNameUrl = s"${config.estatesUrl}/estates/correspondence/name"

  def getEstateName()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Option[String]] = {
    http.GET[EstateName](getEstateNameUrl).map(_.name)
  }

  private val getRegistrationUrl = s"${config.estatesUrl}/estates/registration"

  def getRegistration()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[EstateRegistrationNoDeclaration] = {
    http.GET[EstateRegistrationNoDeclaration](getRegistrationUrl)
  }

  private val getIsLiableForTaxUrl = s"${config.estatesUrl}/estates/is-tax-required"

  def getIsLiableForTax()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Boolean] = {
    http.GET[Boolean](getIsLiableForTaxUrl)
  }

}
