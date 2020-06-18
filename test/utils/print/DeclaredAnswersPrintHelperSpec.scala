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

package utils.print

import java.time.LocalDate

import base.SpecBase
import models.{Correspondence, DeceasedPerson, Declaration, EntitiesType, Estate, EstateRegistration, IdCard, IndividualPersonalRep, Name, NationalInsuranceNumber, NonUkAddress, Passport, PersonalRepresentativeType, UkAddress}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class DeclaredAnswersPrintHelperSpec extends SpecBase {

  val helper: DeclaredAnswersPrintHelper = injector.instanceOf[DeclaredAnswersPrintHelper]

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

  val registration: EstateRegistration = EstateRegistration(
    matchData = None,
    correspondence = Correspondence(
      abroadIndicator = false,
      name = estateName,
      address = ukAddress,
      phoneNumber = phoneNumber
    ),
    yearsReturns = None,
    declaration = Declaration(
      name = name
    ),
    estate = Estate(
      entities = EntitiesType(
        personalRepresentative = PersonalRepresentativeType(
          estatePerRepInd = Some(
            IndividualPersonalRep(
              name,
              dateOfBirth,
              nino,
              ukAddress,
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
            AnswerRow(label = Html(messages("estateDetails.name.checkYourAnswersLabel")), answer = Html("Estate of John Doe"), None)
          )
        ),
        AnswerSection(
          headingKey = Some("taskList.personalRepresentative.label"),
          rows = Seq(
            AnswerRow(label = Html(messages("personalRep.individualOrBusiness.checkYourAnswersLabel")), answer = Html("Individual"), None),
            AnswerRow(label = Html(messages("personalRep.individual.name.checkYourAnswersLabel")), answer = Html("John Doe"), None),
            AnswerRow(label = Html(messages("personalRep.individual.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("3 February 2000"), None),
            AnswerRow(label = Html(messages("personalRep.individual.ninoYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), None),
            AnswerRow(label = Html(messages("personalRep.individual.nino.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), None),
            AnswerRow(label = Html(messages("personalRep.individual.livesInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), None),
            AnswerRow(label = Html(messages("personalRep.individual.address.checkYourAnswersLabel", name.displayName)), answer = Html("21 Test Lane<br />Testville<br />NE1 1NE"), None),
            AnswerRow(label = Html(messages("personalRep.individual.telephoneNumber.checkYourAnswersLabel", name.displayName)), answer = Html("+447123456789"), None)
          )
        ),
        AnswerSection(
          headingKey = Some("taskList.personWhoDied.label"),
          rows = Seq(
            AnswerRow(label = Html(messages("deceasedPerson.name.checkYourAnswersLabel")), answer = Html("John Doe"), None),
            AnswerRow(label = Html(messages("deceasedPerson.dateOfDeath.checkYourAnswersLabel", name.displayName)), answer = Html("3 February 2019"), None),
            AnswerRow(label = Html(messages("deceasedPerson.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), None),
            AnswerRow(label = Html(messages("deceasedPerson.ninoYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), None),
            AnswerRow(label = Html(messages("deceasedPerson.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), None)
          )
        )
      )
    }
  }
}
