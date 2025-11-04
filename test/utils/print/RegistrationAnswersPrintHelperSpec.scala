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

package utils.print

import java.time.LocalDate

import base.SpecBase
import models._
import models.entities.personalrep.{IndividualPersonalRep, PersonalRepresentativeType}
import models.entities.{DeceasedPerson, EntitiesType}
import models.identification.{IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class RegistrationAnswersPrintHelperSpec extends SpecBase {

  val helper: RegistrationAnswersPrintHelper = injector.instanceOf[RegistrationAnswersPrintHelper]

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

  "DeclaredAnswersPrintHelper" must {

    "render answer sections" in {

      val result = helper(registration)

      result mustBe Seq(
        AnswerSection(
          headingKey = Some("taskList.estateName.label"),
          rows = Seq(
            AnswerRow(label = messages("estateDetails.name.checkYourAnswersLabel"), answer = Html("Estate of John Doe"), None)
          )
        ),
        AnswerSection(
          headingKey = Some("taskList.personalRepresentative.label"),
          rows = Seq(
            AnswerRow(label = messages("personalRep.individualOrBusiness.checkYourAnswersLabel"), answer = Html("Individual"), None),
            AnswerRow(label = messages("personalRep.individual.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
            AnswerRow(label = messages("personalRep.individual.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
            AnswerRow(label = messages("personalRep.individual.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
            AnswerRow(label = messages("personalRep.individual.nino.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), None),
            AnswerRow(label = messages("personalRep.individual.livesInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
            AnswerRow(label = messages("personalRep.individual.address.checkYourAnswersLabel", name.displayName), answer = Html("21 Test Lane<br />Testville<br />NE1 1NE"), None),
            AnswerRow(label = messages("personalRep.individual.emailYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
            AnswerRow(label = messages("personalRep.individual.telephoneNumber.checkYourAnswersLabel", name.displayName), answer = Html("+447123456789"), None)
          )
        ),
        AnswerSection(
          headingKey = Some("taskList.personWhoDied.label"),
          rows = Seq(
            AnswerRow(label = messages("deceasedPerson.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
            AnswerRow(label = messages("deceasedPerson.dateOfDeath.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2019"), None),
            AnswerRow(label = messages("deceasedPerson.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
            AnswerRow(label = messages("deceasedPerson.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
            AnswerRow(label = messages("deceasedPerson.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None)
          )
        )
      )
    }
  }
}
