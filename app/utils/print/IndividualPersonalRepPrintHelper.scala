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

import models.entities.personalrep.IndividualPersonalRep
import models.identification._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class IndividualPersonalRepPrintHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def apply(individual: IndividualPersonalRep)(implicit messages: Messages): AnswerSection = {
    val converter = AnswerRowConverter(individual.name.displayName)(checkAnswersFormatters)

    def identification(identification: IndividualIdentification): Seq[AnswerRow] = {
      identification match {
        case NationalInsuranceNumber(nino) =>
          Seq(
            converter.yesNoQuestion(boolean = true, "personalRep.individual.ninoYesNo"),
            converter.ninoQuestion(nino, "personalRep.individual.nino")
          )
        case passport: Passport =>
          Seq(
            converter.yesNoQuestion(boolean = false, "personalRep.individual.ninoYesNo"),
            converter.stringQuestion(messages("passportOrIdCard.passport"), "personalRep.individual.passportOrIdCard"),
            converter.passportQuestion(passport, "personalRep.individual.passport")
          )
        case idCard: IdCard =>
          Seq(
            converter.yesNoQuestion(boolean = false, "personalRep.individual.ninoYesNo"),
            converter.stringQuestion(messages("passportOrIdCard.idCard"), "personalRep.individual.passportOrIdCard"),
            converter.idCardQuestion(idCard, "personalRep.individual.idCard")
          )
      }
    }

    def address(address: Address): Seq[AnswerRow] = {
      def answerRow(isUk: Boolean, address: Address): Seq[AnswerRow] = Seq(
        converter.yesNoQuestion(isUk, "personalRep.individual.livesInTheUkYesNo"),
        converter.addressQuestion(address, "personalRep.individual.address")
      )

      address match {
        case address: UkAddress =>
          answerRow(isUk = true, address)
        case address: NonUkAddress =>
          answerRow(isUk = false, address)
      }
    }

    def email(email: Option[String]): Seq[AnswerRow] = {
      Seq(
        Some(converter.yesNoQuestion(boolean = email.isDefined, "personalRep.individual.emailYesNo")),
        converter.optionStringQuestion(email, "personalRep.individual.email")
      ).flatten
    }

    val rows: Seq[AnswerRow] =
      Seq(
        converter.stringQuestion(messages("individualOrBusiness.individual"), "personalRep.individualOrBusiness"),
        converter.nameQuestion(individual.name, "personalRep.individual.name"),
        converter.dateQuestion(individual.dateOfBirth, "personalRep.individual.dateOfBirth")
      ) ++
        identification(individual.identification) ++
        address(individual.address) ++
        email(individual.email) ++
        Seq(converter.stringQuestion(individual.phoneNumber, "personalRep.individual.telephoneNumber"))

    AnswerSection(Some("taskList.personalRepresentative.label"), rows)
  }
}
