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

import com.google.inject.Inject
import models.{Address, IdCard, Name, Passport}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.countryOptions.CountryOptions
import utils.print.CheckAnswersFormatters._
import viewmodels.AnswerRow

case class AnswerRowConverter @Inject()(countryOptions: CountryOptions, name: String = "")
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

  def yesNoQuestion(boolean: Boolean,
                    labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      yesOrNo(boolean)
    )
  }

  def dateQuestion(date: LocalDate,
                   labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      HtmlFormat.escape(date.format(dateFormatter))
    )
  }

  def ninoQuestion(nino: String,
                   labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      formatNino(nino)
    )
  }

  def addressQuestion[T <: Address](address: T,
                                    labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      formatAddress(address, countryOptions)
    )
  }

  def passportQuestion(passport: Passport,
                       labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      formatPassportDetails(passport, countryOptions)
    )
  }

  def idCardQuestion(idCard: IdCard,
                     labelKey: String): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
      formatIdCardDetails(idCard, countryOptions)
    )
  }
}
