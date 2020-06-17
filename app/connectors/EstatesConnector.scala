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

import java.time.LocalDate

import config.FrontendAppConfig
import javax.inject.Inject
import models._
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
  private val getRegistrationEnabled: Boolean = false

  def getRegistration()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[EstateRegistration] = {
    if (getRegistrationEnabled) {
      http.GET[EstateRegistration](getRegistrationUrl)
    } else {
      Future.successful(
        registration
      )
    }
  }

  private val registration: EstateRegistration = EstateRegistration(
    matchData = None,
    correspondence = Correspondence(
      abroadIndicator = false,
      name = "name",
      address = UkAddress("line1", "line2", None, None, "NE22NE"),
      phoneNumber = "123"
    ),
    yearsReturns = None,
    declaration = Declaration(
      name = Name("first", None, "last")
    ),
    estate = Estate(
      entities = EntitiesType(
        PersonalRepresentativeType(
          Some(IndividualPersonalRep(
            Name("first", None, "last"),
            LocalDate.parse("1996-02-03"),
            NationalInsuranceNumber("AA000000A"),
            UkAddress("line1", "line2", None, None, "NE11NE"),
            "999"
          )),
          None
        ),
        DeceasedPerson(
          Name("first", None, "last"),
          Some(LocalDate.parse("1996-02-03")),
          LocalDate.parse("2020-02-03"),
          nino = Some(NationalInsuranceNumber("BB000000B")),
          None
        )
      ),
      administrationEndDate = None,
      periodTaxDues = "periodTaxDues"
    ),
    agentDetails = None
  )

}
