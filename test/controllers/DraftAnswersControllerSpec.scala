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

package controllers

import java.time.LocalDate
import base.SpecBase
import connectors.EstatesConnector
import models._
import models.entities.personalrep.{IndividualPersonalRep, PersonalRepresentativeType}
import models.entities.{DeceasedPerson, EntitiesType}
import models.identification.{IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.print.RegistrationAnswersPrintHelper
import views.html.DraftAnswersView

import scala.concurrent.Future

class DraftAnswersControllerSpec extends SpecBase {

  val estateName: String = "Estate of John Doe"
  val name: Name = Name("John", None, "Doe")
  val dateOfBirth: LocalDate = LocalDate.parse("2000-02-03")
  val dateOfDeath: LocalDate = LocalDate.parse("2019-02-03")
  val nino: NationalInsuranceNumber = NationalInsuranceNumber("AA000000A")
  val ukAddress: UkAddress = UkAddress("21 Test Lane", "Testville", None, None, "NE1 1NE")
  val nonUkAddress: NonUkAddress = NonUkAddress("99 Test Lane", "Testville", None, "DE")
  val passport: Passport = Passport("GB", "1234567890", LocalDate.parse("2023-02-03"))
  val idCard: IdCard = IdCard("GB", "1234567890", LocalDate.parse("2023-02-03"))
  val phoneNumber: String = "+447123456789"

  val registration: EstateRegistrationNoDeclaration = EstateRegistrationNoDeclaration(
    matchData = None,
    correspondence = CorrespondenceName(
      name = estateName
    ),
    yearsReturns = None,
    estate = Estate(
      entities = EntitiesType(
        personalRepresentative = PersonalRepresentativeType(
          estatePerRepInd = Some(
            IndividualPersonalRep(
              name,
              dateOfBirth,
              nino,
              ukAddress,
              None,
              phoneNumber
            )
          ),
          estatePerRepOrg = None
        ),
        deceased = DeceasedPerson(
          name,
          None,
          dateOfDeath,
          None,
          Some(false),
          None
        )
      ),
      administrationEndDate = None,
      periodTaxDues = ""
    ),
    agentDetails = None
  )

  val mockConnector: EstatesConnector = mock[EstatesConnector]

  "DraftAnswersController" must {

    "return OK and the correct view for a GET" in {

      val entities = injector.instanceOf[RegistrationAnswersPrintHelper]

      when(mockConnector.getRegistration()(any(), any())).thenReturn(Future.successful(registration))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[EstatesConnector].to(mockConnector))
        .build()

      val request = FakeRequest(GET, routes.DraftAnswersController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DraftAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(entities(registration))(request, messages).toString

      application.stop()
    }
  }

}
