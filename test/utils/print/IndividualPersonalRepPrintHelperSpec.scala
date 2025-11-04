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
import models.entities.personalrep
import models.entities.personalrep.IndividualPersonalRep
import models.identification._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class IndividualPersonalRepPrintHelperSpec extends SpecBase {

  val helper: IndividualPersonalRepPrintHelper = injector.instanceOf[IndividualPersonalRepPrintHelper]

  val name: Name = Name("John", None, "Doe")
  val dateOfBirth: LocalDate = LocalDate.parse("2000-02-03")
  val nino: NationalInsuranceNumber = NationalInsuranceNumber("AA000000A")
  val ukAddress: UkAddress = UkAddress("21 Test Lane", "Testville", None, None, "NE1 1NE")
  val nonUkAddress: NonUkAddress = NonUkAddress("99 Test Lane", "Testville", None, "DE")
  val passport: Passport = Passport("GB", "1234567890", LocalDate.parse("2023-02-03"))
  val idCard: IdCard = IdCard("GB", "1234567890", LocalDate.parse("2023-02-03"))
  val email: Option[String] = Some("email@example.com")
  val phoneNumber: String = "+447123456789"

  "IndividualPersonalRepPrintHelper" must {

    "render answer section for individual personal rep with a NINO, UK address and an email address" in {

      val personalRep: IndividualPersonalRep = personalrep.IndividualPersonalRep(
        name,
        dateOfBirth,
        nino,
        ukAddress,
        email,
        phoneNumber
      )

      val result = helper(personalRep)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personalRepresentative.label"),
        rows = Seq(
          AnswerRow(label = messages("personalRep.individualOrBusiness.checkYourAnswersLabel"), answer = Html("Individual"), None),
          AnswerRow(label = messages("personalRep.individual.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("personalRep.individual.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
          AnswerRow(label = messages("personalRep.individual.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.individual.nino.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), None),
          AnswerRow(label = messages("personalRep.individual.livesInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.individual.address.checkYourAnswersLabel", name.displayName), answer = Html("21 Test Lane<br />Testville<br />NE1 1NE"), None),
          AnswerRow(label = messages("personalRep.individual.emailYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.individual.email.checkYourAnswersLabel", name.displayName), answer = Html("email@example.com"), None),
          AnswerRow(label = messages("personalRep.individual.telephoneNumber.checkYourAnswersLabel", name.displayName), answer = Html("+447123456789"), None)
        )
      )
    }

    "render answer section for individual personal rep with a passport and UK address" in {

      val personalRep: IndividualPersonalRep = personalrep.IndividualPersonalRep(
        name,
        dateOfBirth,
        passport,
        ukAddress,
        None,
        phoneNumber
      )

      val result = helper(personalRep)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personalRepresentative.label"),
        rows = Seq(
          AnswerRow(label = messages("personalRep.individualOrBusiness.checkYourAnswersLabel"), answer = Html("Individual"), None),
          AnswerRow(label = messages("personalRep.individual.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("personalRep.individual.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
          AnswerRow(label = messages("personalRep.individual.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.individual.passportOrIdCard.checkYourAnswersLabel", name.displayName), answer = Html("Passport"), None),
          AnswerRow(label = messages("personalRep.individual.passport.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234567890<br />3 February 2023"), None),
          AnswerRow(label = messages("personalRep.individual.livesInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.individual.address.checkYourAnswersLabel", name.displayName), answer = Html("21 Test Lane<br />Testville<br />NE1 1NE"), None),
          AnswerRow(label = messages("personalRep.individual.emailYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.individual.telephoneNumber.checkYourAnswersLabel", name.displayName), answer = Html("+447123456789"), None)
        )
      )
    }

    "render answer section for individual personal rep with an ID card and non-UK address" in {

      val personalRep: IndividualPersonalRep = personalrep.IndividualPersonalRep(
        name,
        dateOfBirth,
        idCard,
        nonUkAddress,
        None,
        phoneNumber
      )

      val result = helper(personalRep)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personalRepresentative.label"),
        rows = Seq(
          AnswerRow(label = messages("personalRep.individualOrBusiness.checkYourAnswersLabel"), answer = Html("Individual"), None),
          AnswerRow(label = messages("personalRep.individual.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("personalRep.individual.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
          AnswerRow(label = messages("personalRep.individual.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.individual.passportOrIdCard.checkYourAnswersLabel", name.displayName), answer = Html("ID card"), None),
          AnswerRow(label = messages("personalRep.individual.idCard.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234567890<br />3 February 2023"), None),
          AnswerRow(label = messages("personalRep.individual.livesInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.individual.address.checkYourAnswersLabel", name.displayName), answer = Html("99 Test Lane<br />Testville<br />Germany"), None),
          AnswerRow(label = messages("personalRep.individual.emailYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.individual.telephoneNumber.checkYourAnswersLabel", name.displayName), answer = Html("+447123456789"), None)
        )
      )
    }
  }
}
