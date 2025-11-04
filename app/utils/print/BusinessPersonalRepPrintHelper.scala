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

import models.entities.personalrep.BusinessPersonalRep
import models.identification.{Address, NonUkAddress, UkAddress}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class BusinessPersonalRepPrintHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def apply(business: BusinessPersonalRep)(implicit messages: Messages): AnswerSection = {
    val converter = AnswerRowConverter(business.name)(checkAnswersFormatters)

    def name(utr: Option[String]): Seq[AnswerRow] = {
      Seq(
        Some(converter.yesNoQuestion(boolean = utr.isDefined, "personalRep.business.ukRegisteredYesNo")),
        Some(converter.stringQuestion(business.name, "personalRep.business.name")),
        converter.optionStringQuestion(utr, "personalRep.business.utr")
      ).flatten
    }

    def address(address: Address): Seq[AnswerRow] = {
      def answerRow(isUk: Boolean, address: Address): Seq[AnswerRow] = Seq(
        converter.yesNoQuestion(isUk, "personalRep.business.addressUkYesNo"),
        converter.addressQuestion(address, "personalRep.business.address")
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
        Some(converter.yesNoQuestion(boolean = email.isDefined, "personalRep.business.emailYesNo")),
        converter.optionStringQuestion(email, "personalRep.business.email")
      ).flatten
    }

    val rows: Seq[AnswerRow] =
      Seq(converter.stringQuestion(messages("individualOrBusiness.business"), "personalRep.individualOrBusiness")) ++
        name(business.utr) ++
        address(business.address) ++
        email(business.email) ++
        Seq(converter.stringQuestion(business.phoneNumber, "personalRep.business.telephoneNumber"))

    AnswerSection(Some("taskList.personalRepresentative.label"), rows)
  }
}
