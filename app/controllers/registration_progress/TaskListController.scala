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

package controllers.registration_progress

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.{EstatesConnector, EstatesStoreConnector}
import controllers.actions.Actions
import handlers.ErrorHandler
import models.{CompletedTasks, CompletedTasksResponse, UserAnswers}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                    actions: Actions,
                                    val controllerComponents: MessagesControllerComponents,
                                    val config: FrontendAppConfig,
                                    view: TaskListView,
                                    connector: EstatesConnector,
                                    storeConnector: EstatesStoreConnector,
                                    errorHandler: ErrorHandler,
                                    repository: SessionRepository
                                  )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with TaskListSections with Logging {

  def onPageLoad(): Action[AnyContent] = actions.authWithSession.async {
    implicit request =>
      def continueOrCreateNewSession =
        request.userAnswers.getOrElse(UserAnswers(request.internalId))

      def getEstateName = connector.getEstateName()

      def getIsLiableForTax = connector.getIsLiableForTax()

      def taskList(estateName: Option[String], isLiableForTax: Boolean, tasks: CompletedTasksResponse): Future[Result] = {
        tasks match {
          case l @ CompletedTasks(_, _, _, _) =>
            val taskList = generateTaskList(l, isLiableForTax)
            Future.successful(Ok(view(
              estateName = estateName,
              sections = taskList.mandatory,
              isTaskListComplete = taskList.isAbleToDeclare,
              affinityGroup = request.affinityGroup)))
          case CompletedTasksResponse.InternalServerError =>
            logger.error(s"[TaskListController] unable to get tasks statuses")
            errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
        }
      }

      for {
        _ <- repository.set(continueOrCreateNewSession)
        estateName <- getEstateName
        isLiableForTax <- getIsLiableForTax
        tasks <- storeConnector.getStatusOfTasks
        result <- taskList(estateName, isLiableForTax, tasks)
      } yield result
  }


  def onSubmit: Action[AnyContent] = Action {
    Redirect(controllers.routes.DeclarationController.onPageLoad())
  }
}
