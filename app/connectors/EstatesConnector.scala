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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.{Declaration, EstatePerRepIndType, EstatePerRepOrgType, EstateRegistration, PersonalRepresentativeType}
import models.http.DeclarationResponse
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val registerUrl = s"${config.estatesUrl}/estates/register"

  def register(payload: EstateRegistration)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[DeclarationResponse] = {
    http.POST[EstateRegistration, DeclarationResponse](registerUrl, payload)
  }

  private val getPersonalRepIndUrl = s"${config.estatesUrl}/estates/personal-rep/individual"
  private val getPersonalRepOrgUrl = s"${config.estatesUrl}/estates/personal-rep/organisation"

  def getPersonalRep()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PersonalRepresentativeType] = {
    for {
      individual <- http.GET[Option[EstatePerRepIndType]](getPersonalRepIndUrl)
      organisation <- http.GET[Option[EstatePerRepOrgType]](getPersonalRepOrgUrl)
    } yield {
      (individual, organisation) match {
        case (ind: Option[EstatePerRepIndType], _) => PersonalRepresentativeType(ind, None)
        case (_, org: Option[EstatePerRepOrgType]) => PersonalRepresentativeType(None, org)
      }
    }
  }
}


