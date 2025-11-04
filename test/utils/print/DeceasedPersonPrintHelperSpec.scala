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
import models.entities.DeceasedPerson
import models.identification.{Name, NationalInsuranceNumber, NonUkAddress, UkAddress}
import models.entities
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class DeceasedPersonPrintHelperSpec extends SpecBase {

  val helper: DeceasedPersonPrintHelper = injector.instanceOf[DeceasedPersonPrintHelper]

  val name: Name = Name("John", None, "Doe")
  val dateOfBirth: Option[LocalDate] = Some(LocalDate.parse("2000-02-03"))
  val dateOfDeath: LocalDate = LocalDate.parse("2019-02-03")
  val nino: Option[NationalInsuranceNumber] = Some(NationalInsuranceNumber("AA000000A"))
  val ukAddress: Option[UkAddress] = Some(UkAddress("21 Test Lane", "Testville", None, None, "NE1 1NE"))
  val nonUkAddress: Option[NonUkAddress] = Some(NonUkAddress("99 Test Lane", "Testville", None, "DE"))

  "DeceasedPersonPrintHelper" must {

    "render answer section for deceased person with no date of birth, address or NINO" in {

      val deceased: DeceasedPerson = entities.DeceasedPerson(
        name,
        None,
        dateOfDeath,
        None,
        Some(false),
        None
      )

      val result = helper(deceased)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personWhoDied.label"),
        rows = Seq(
          AnswerRow(label = messages("deceasedPerson.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfDeath.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2019"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("deceasedPerson.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("deceasedPerson.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None)
        )
      )
    }

    "render answer section for deceased person with a date of birth, UK address and no NINO" in {

      val deceased: DeceasedPerson = entities.DeceasedPerson(
        name,
        dateOfBirth,
        dateOfDeath,
        None,
        Some(true),
        ukAddress
      )

      val result = helper(deceased)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personWhoDied.label"),
        rows = Seq(
          AnswerRow(label = messages("deceasedPerson.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfDeath.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2019"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
          AnswerRow(label = messages("deceasedPerson.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("deceasedPerson.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.livedInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.address.checkYourAnswersLabel", name.displayName), answer = Html("21 Test Lane<br />Testville<br />NE1 1NE"), None)
        )
      )
    }

    "render answer section for deceased person with a date of birth, non-UK address and no NINO" in {

      val deceased: DeceasedPerson = entities.DeceasedPerson(
        name,
        dateOfBirth,
        dateOfDeath,
        None,
        Some(true),
        nonUkAddress
      )

      val result = helper(deceased)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personWhoDied.label"),
        rows = Seq(
          AnswerRow(label = messages("deceasedPerson.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfDeath.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2019"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
          AnswerRow(label = messages("deceasedPerson.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("deceasedPerson.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.livedInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), None),
          AnswerRow(label = messages("deceasedPerson.address.checkYourAnswersLabel", name.displayName), answer = Html("99 Test Lane<br />Testville<br />Germany"), None)
        )
      )
    }

    "render answer section for deceased person with a date of birth and a NINO" in {

      val deceased: DeceasedPerson = entities.DeceasedPerson(
        name,
        dateOfBirth,
        dateOfDeath,
        nino,
        None,
        None
      )

      val result = helper(deceased)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personWhoDied.label"),
        rows = Seq(
          AnswerRow(label = messages("deceasedPerson.name.checkYourAnswersLabel"), answer = Html("John Doe"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfDeath.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2019"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("3 February 2000"), None),
          AnswerRow(label = messages("deceasedPerson.ninoYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), None),
          AnswerRow(label = messages("deceasedPerson.nino.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), None)
        )
      )
    }
  }
}
