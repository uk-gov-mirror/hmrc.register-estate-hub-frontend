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

package utils

import java.time.format.DateTimeFormatter

import models.UserAnswers
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  private def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }

  def estateDetails: Option[Seq[AnswerSection]] = {
    val questions = Seq(
      estateName
    ).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(messages("answerPage.section.estateDetails.heading"))))) else None
  }

  def personalRepresentative: Option[Seq[AnswerSection]] = {
    val questions = Seq(
      personalRepIndividualOrBusiness
    ).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(messages("answerPage.section.personalRepresentative.heading"))))) else None
  }



  def estateName: Option[AnswerRow] = userAnswers.get(EstateNamePage) map {
    x => AnswerRow("estateName.checkYourAnswersLabel", HtmlFormat.escape(x), Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url))
  }

  def personalRepIndividualOrBusiness: Option[AnswerRow] = userAnswers.get(PersonalRepIndividualOrBusinessPage) map {
    x =>
      AnswerRow(
        "personalRepIndividualOrBusiness.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"personalRepIndividualOrBusiness.$x")),
        Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url)
      )
  }


}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
