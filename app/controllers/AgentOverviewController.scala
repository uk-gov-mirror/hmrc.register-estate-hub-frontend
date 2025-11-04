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

import config.FrontendAppConfig
import controllers.actions._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AgentOverviewView

import scala.concurrent.Future

class AgentOverviewController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       val appConfig: FrontendAppConfig,
                                       identify: IdentifierAction,
                                       hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AgentOverviewView
                                     ) extends FrontendBaseController with I18nSupport {

  private def actions() = identify andThen hasAgentAffinityGroup()

  def onPageLoad(): Action[AnyContent] = actions() {
    implicit request =>
      Ok(view())
  }

  def onSubmit(): Action[AnyContent] = actions().async {
      Future.successful(Redirect(routes.EstateRegisteredOnlineYesNoController.onPageLoad()))
  }

}
