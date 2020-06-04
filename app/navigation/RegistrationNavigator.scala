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

package navigation

import controllers.{routes => rts}
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class RegistrationNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes()(page)(userAnswers)

//  private def simpleNavigation(): PartialFunction[Page, Call] = {
//    case EstateRegisteredOnlineYesNoPage => rts.EstateRegisteredOnlineYesNoController.onPageLoad()
//
//  }

  private def yesNoNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case EstateRegisteredOnlineYesNoPage => ua =>
      yesNoNav(ua, EstateRegisteredOnlineYesNoPage, rts.FeatureNotAvailableController.onPageLoad(), rts.HaveUTRYesNoController.onPageLoad())
    case HaveUTRYesNoPage => ua =>
      yesNoNav(ua, HaveUTRYesNoPage, rts.FeatureNotAvailableController.onPageLoad(), controllers.task_list.routes.TaskListController.onPageLoad())
  }

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }


  def routes(): PartialFunction[Page, UserAnswers => Call] =
//    simpleNavigation() andThen (c => (_: UserAnswers) => c) orElse
      yesNoNavigation()
}
