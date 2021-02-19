/*
 * Copyright 2021 HM Revenue & Customs
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

import models.entities.DeceasedPerson
import models.identification.{Address, NationalInsuranceNumber, NonUkAddress, UkAddress}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate
import javax.inject.Inject

class DeceasedPersonPrintHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def apply(deceasedPerson: DeceasedPerson)(implicit messages: Messages): AnswerSection = {
    val converter = AnswerRowConverter(deceasedPerson.name.displayName)(checkAnswersFormatters)

    def dateOfBirth(dateOfBirth: Option[LocalDate]): Seq[AnswerRow] = {
      dateOfBirth match {
        case Some(date) => Seq(
          converter.yesNoQuestion(boolean = true, "deceasedPerson.dateOfBirthYesNo"),
          converter.dateQuestion(date, "deceasedPerson.dateOfBirth")
        )
        case _ => Seq(converter.yesNoQuestion(boolean = false, "deceasedPerson.dateOfBirthYesNo"))
      }
    }

    def nino(nino: Option[NationalInsuranceNumber]): Seq[AnswerRow] = {
      nino match {
        case Some(NationalInsuranceNumber(x)) => Seq(
          converter.yesNoQuestion(boolean = true, "deceasedPerson.ninoYesNo"),
          converter.ninoQuestion(x, "deceasedPerson.nino")
        )
        case _ => Seq(converter.yesNoQuestion(boolean = false, "deceasedPerson.ninoYesNo"))
      }
    }

    def address(address: Option[Address]): Seq[AnswerRow] = {
      def answerRow(isUk: Boolean, address: Address): Seq[AnswerRow] = Seq(
        converter.yesNoQuestion(boolean = true, "deceasedPerson.addressYesNo"),
        converter.yesNoQuestion(isUk, "deceasedPerson.livedInTheUkYesNo"),
        converter.addressQuestion(address, "deceasedPerson.address")
      )

      address match {
        case Some(x) => x match {
          case address: UkAddress => answerRow(isUk = true, address)
          case address: NonUkAddress => answerRow(isUk = false, address)
        }
        case _ => Seq(converter.yesNoQuestion(boolean = false, "deceasedPerson.addressYesNo"))
      }
    }

    val rows: Seq[AnswerRow] =
      Seq(
        converter.nameQuestion(deceasedPerson.name, "deceasedPerson.name"),
        converter.dateQuestion(deceasedPerson.dateOfDeath, "deceasedPerson.dateOfDeath")
      ) ++
        dateOfBirth(deceasedPerson.dateOfBirth) ++
        nino(deceasedPerson.nino) ++
        address(deceasedPerson.address)

    AnswerSection(Some("taskList.personWhoDied.label"), rows)
  }
}
