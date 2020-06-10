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

import base.SpecBase
import controllers.routes
import pages._
import models._

class RegistrationNavigatorSpec extends SpecBase {

  val navigator = new RegistrationNavigator

  "Registration navigator" when {

    "EstateRegisteredOnlineYesNo page -> Yes -> FeatureNotAvailable page" in {
      val answers = emptyUserAnswers
        .set(EstateRegisteredOnlineYesNoPage, true).success.value

      navigator.nextPage(EstateRegisteredOnlineYesNoPage, answers)
        .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
    }

    "EstateRegisteredOnlineYesNo page -> No -> HaveUTRYesNo page" in {
      val answers = emptyUserAnswers
        .set(EstateRegisteredOnlineYesNoPage, false).success.value

      navigator.nextPage(EstateRegisteredOnlineYesNoPage, answers)
        .mustBe(controllers.routes.HaveUTRYesNoController.onPageLoad())
    }

    "HaveUTRYesNo page -> Yes -> FeatureNotAvailable page" in {
      val answers = emptyUserAnswers
        .set(HaveUTRYesNoPage, true).success.value

      navigator.nextPage(HaveUTRYesNoPage, answers)
        .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
    }

    "HaveUTRYesNo page -> No -> TaskList page" in {
      val answers = emptyUserAnswers
        .set(HaveUTRYesNoPage, false).success.value

      navigator.nextPage(HaveUTRYesNoPage, answers)
        .mustBe(controllers.registration_progress.routes.TaskListController.onPageLoad())
    }
  }
}
