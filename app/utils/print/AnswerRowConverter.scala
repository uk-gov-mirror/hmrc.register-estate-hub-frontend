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

import com.google.inject.Inject
import models.identification.{Address, IdCard, Name, Passport}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import viewmodels.AnswerRow

import java.time.LocalDate

case class AnswerRowConverter @Inject()(name: String = "")
                                       (checkAnswersFormatters: CheckAnswersFormatters)
                                       (implicit messages: Messages) {

  def nameQuestion(name: Name,
                   labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel")),
      HtmlFormat.escape(name.displayFullName)
    )
  }

  def stringQuestion(string: String,
                     labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      HtmlFormat.escape(string)
    )
  }

  def optionStringQuestion(optionString: Option[String],
                           labelKey: String): Option[AnswerRow] = {
    optionString match {
      case Some(string) => Some(stringQuestion(string, labelKey))
      case _ => None
    }
  }

  def yesNoQuestion(boolean: Boolean,
                    labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      checkAnswersFormatters.yesOrNo(boolean)
    )
  }

  def dateQuestion(date: LocalDate,
                   labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      HtmlFormat.escape(checkAnswersFormatters.formatDate(date))
    )
  }

  def ninoQuestion(nino: String,
                   labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      checkAnswersFormatters.formatNino(nino)
    )
  }

  def addressQuestion[T <: Address](address: T,
                                    labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      checkAnswersFormatters.formatAddress(address)
    )
  }

  def passportQuestion(passport: Passport,
                       labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      checkAnswersFormatters.formatPassportDetails(passport)
    )
  }

  def idCardQuestion(idCard: IdCard,
                     labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      checkAnswersFormatters.formatIdCardDetails(idCard)
    )
  }
}
