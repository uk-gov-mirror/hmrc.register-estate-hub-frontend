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

import controllers.actions.Actions
import javax.inject.Inject
import models.UserAnswers
import models.requests.OptionalDataRequest
import pages.{EstateRegisteredOnlineYesNoPage, HaveUTRYesNoPage}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 actions: Actions,
                                 repository: SessionRepository
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = actions.authWithSession.async {
    implicit request =>
      request.affinityGroup match {
        case AffinityGroup.Agent =>
          Logger.info(s"[IndexController] user is an agent, redirect to overview")
          val route: Call = controllers.routes.AgentOverviewController.onPageLoad()
          checkUserAnswersAndRedirect(request, route)
        case _ =>
          val route: Call = controllers.routes.EstateRegisteredOnlineYesNoController.onPageLoad()
          checkUserAnswersAndRedirect(request, route)
      }
  }

  private def checkUserAnswersAndRedirect(request: OptionalDataRequest[AnyContent], route: Call): Future[Result] = {
    request.userAnswers match {
      case Some(userAnswers) =>
        for {
          updatedAnswers <- Future.fromTry(
            userAnswers
              .remove(EstateRegisteredOnlineYesNoPage)
              .flatMap(_.remove(HaveUTRYesNoPage))
          )
          _ <- repository.set(updatedAnswers)
        } yield Redirect(route)
      case None =>
        val userAnswers: UserAnswers = UserAnswers(request.internalId)
        repository.set(userAnswers).map { _ =>
          Redirect(route)
        }
    }
  }

}
