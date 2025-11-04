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

package controllers

import java.time.LocalDateTime

import connectors.EstatesConnector
import controllers.actions.Actions
import javax.inject.Inject
import models.requests.DataRequest
import pages.{SubmissionDatePage, TRNPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.DateFormatter
import utils.print.RegistrationAnswersPrintHelper
import views.html.DeclaredAnswersView

import scala.concurrent.ExecutionContext

class DeclaredAnswersController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           actions: Actions,
                                           val controllerComponents: MessagesControllerComponents,
                                           declaredAnswersView: DeclaredAnswersView,
                                           printHelper: RegistrationAnswersPrintHelper,
                                           dateFormatter: DateFormatter,
                                           connector: EstatesConnector
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      connector.getRegistration() map { registration =>

        val entities = printHelper(registration)

        val trn = request.userAnswers.get(TRNPage).getOrElse("")

        Ok(declaredAnswersView(entities, trn, declarationSent))
      }
  }

  private def declarationSent(implicit request: DataRequest[AnyContent]): String = {
    val trnDateTime = request.userAnswers.get(SubmissionDatePage).getOrElse(LocalDateTime.now)
    dateFormatter.formatDate(trnDateTime)
  }

}