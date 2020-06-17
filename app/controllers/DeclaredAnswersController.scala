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
import models.requests.DataRequest
import pages.{SubmissionDatePage, TRNPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.DateFormatter
import utils.print.DeclaredAnswersPrintHelper
import views.html.DeclaredAnswersView

import scala.concurrent.{ExecutionContext, Future}

class DeclaredAnswersController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           actions: Actions,
                                           val controllerComponents: MessagesControllerComponents,
                                           declaredAnswersView: DeclaredAnswersView,
                                           printHelper: DeclaredAnswersPrintHelper,
                                           dateFormatter: DateFormatter
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      val entities = printHelper.entities()

      val tvn = request.userAnswers.get(TRNPage).getOrElse("")

      Future.successful(Ok(declaredAnswersView(entities, tvn, declarationSent)))
  }

  private def declarationSent(implicit request: DataRequest[AnyContent]): String = {
    val trnDateTime = request.userAnswers.get(SubmissionDatePage).getOrElse(LocalDateTime.now)
    dateFormatter.formatDate(trnDateTime)
  }

}