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

package controllers

import java.time.LocalDateTime

import controllers.actions.Actions
import javax.inject.Inject
import pages.{SubmissionDatePage, TRNPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.DateFormatter
import utils.print.PrintUserAnswersHelper
import views.html.DeclaredPrintPageView


class DeclaredPrintPageController @Inject()(override val messagesApi: MessagesApi,
                                            val controllerComponents: MessagesControllerComponents,
                                            actions: Actions,
                                            view: DeclaredPrintPageView,
                                            printUserAnswersHelper: PrintUserAnswersHelper,
                                            dateFormatter: DateFormatter
                                            ) extends FrontendBaseController with I18nSupport {

  def onPageLoad() = actions.authWithData {
    implicit request =>

      val sections = printUserAnswersHelper.summary(request.userAnswers)

      val trn = request.userAnswers.get(TRNPage).getOrElse("")

      val trnDateTime = request.userAnswers.get(SubmissionDatePage).getOrElse(LocalDateTime.now)

      val declarationSent : String = dateFormatter.formatDate(trnDateTime)

      Ok(view(sections, trn, declarationSent))

  }

}