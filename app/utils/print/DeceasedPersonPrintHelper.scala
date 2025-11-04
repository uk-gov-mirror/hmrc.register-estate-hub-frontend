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

import models.entities.DeceasedPerson
import models.identification.{NationalInsuranceNumber, NonUkAddress, UkAddress}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate
import javax.inject.Inject

class DeceasedPersonPrintHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def dateOfBirth(dateOfBirth: Option[LocalDate], converter: AnswerRowConverter): Seq[AnswerRow] = {
    dateOfBirth match {
      case Some(date) => Seq(
        converter.yesNoQuestion(boolean = true, "deceasedPerson.dateOfBirthYesNo"),
        converter.dateQuestion(date, "deceasedPerson.dateOfBirth")
      )
      case _ => Seq(converter.yesNoQuestion(boolean = false, "deceasedPerson.dateOfBirthYesNo"))
    }
  }

  def nino(nino: Option[NationalInsuranceNumber], converter: AnswerRowConverter): Seq[AnswerRow] = {
    nino match {
      case Some(NationalInsuranceNumber(x)) => Seq(
        converter.yesNoQuestion(boolean = true, "deceasedPerson.ninoYesNo"),
        converter.ninoQuestion(x, "deceasedPerson.nino")
      )
      case _ => Seq(converter.yesNoQuestion(boolean = false, "deceasedPerson.ninoYesNo"))
    }
  }

  def address(deceasedPerson: DeceasedPerson, converter: AnswerRowConverter): Seq[AnswerRow] = {

    val addressYesNoRow: Option[AnswerRow] = deceasedPerson.nino match {
      case None => Some(converter.yesNoQuestion(boolean = deceasedPerson.addressYesNo.get, "deceasedPerson.addressYesNo"))
      case _ => None
    }

    val addressUkYesNoRow: Option[AnswerRow] = deceasedPerson.address.map {
      case _: UkAddress => converter.yesNoQuestion(boolean = true, "deceasedPerson.livedInTheUkYesNo")
      case _: NonUkAddress => converter.yesNoQuestion(boolean = false, "deceasedPerson.livedInTheUkYesNo")
    }

    val addressRow: Option[AnswerRow] = deceasedPerson.address.map { _ =>
      converter.addressQuestion(deceasedPerson.address.get, "deceasedPerson.address")
    }

    (addressYesNoRow ++ addressUkYesNoRow ++ addressRow).toSeq
  }

  def apply(deceasedPerson: DeceasedPerson)(implicit messages: Messages): AnswerSection = {
    val converter = AnswerRowConverter(deceasedPerson.name.displayName)(checkAnswersFormatters)

    val rows: Seq[AnswerRow] =
      Seq(
        converter.nameQuestion(deceasedPerson.name, "deceasedPerson.name"),
        converter.dateQuestion(deceasedPerson.dateOfDeath, "deceasedPerson.dateOfDeath")
      ) ++
        dateOfBirth(deceasedPerson.dateOfBirth, converter) ++
        nino(deceasedPerson.nino, converter) ++
        address(deceasedPerson, converter)

    AnswerSection(Some("taskList.personWhoDied.label"), rows)
  }
}
