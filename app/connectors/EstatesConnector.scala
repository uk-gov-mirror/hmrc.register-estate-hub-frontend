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

package connectors

import config.FrontendAppConfig
import models._
import models.http.DeclarationResponse
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClientV2, config: FrontendAppConfig) {


  def register(payload: Declaration)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[DeclarationResponse] = {
    val registerUrl = s"${config.estatesUrl}/estates/register"
    http.post(url"$registerUrl").withBody(Json.toJson(payload)).execute[DeclarationResponse](
      DeclarationResponse.httpReads, ec)
  }

  def getPersonalRepName()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PersonalRepName] = {
    val getPersonalRepIndUrl = s"${config.estatesUrl}/estates/personal-rep/individual"
    val getPersonalRepOrgUrl = s"${config.estatesUrl}/estates/personal-rep/organisation"
    for {
      individual <- http.get(url"$getPersonalRepIndUrl").execute[Option[PersonalRepName]](PersonalRepName.personalRepIndividualNameReads, ec)
      organisation <- http.get(url"$getPersonalRepOrgUrl").execute[Option[PersonalRepName]](PersonalRepName.personalRepOrganisationNameReads, ec)
    } yield {
      (individual, organisation) match {
        case (Some(name), None) => name
        case (None, Some(name)) => name
        case _ => throw new RuntimeException("Unable to get personal representatives name")
      }
    }
  }


  def getEstateName()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Option[String]] = {
    val getEstateNameUrl = s"${config.estatesUrl}/estates/correspondence/name"
    http.get(url"$getEstateNameUrl").execute[EstateName].map(_.name)
  }


  def getRegistration()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[EstateRegistrationNoDeclaration] = {
    val getRegistrationUrl = s"${config.estatesUrl}/estates/registration"
    http.get(url"$getRegistrationUrl").execute[EstateRegistrationNoDeclaration]
  }


  def getIsLiableForTax()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Boolean] = {
    val getIsLiableForTaxUrl = s"${config.estatesUrl}/estates/is-tax-required"
    http.get(url"$getIsLiableForTaxUrl").execute[Boolean]
  }

}
