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

package navigation

import base.SpecBase
import config.FrontendAppConfig
import pages._

class RegistrationNavigatorSpec extends SpecBase {

  val appConfig = injector.instanceOf[FrontendAppConfig]

  val navigator = new RegistrationNavigator(appConfig)

  "Registration navigator" when {

    "EstateRegisteredOnlineYesNo page -> HaveUTRYesNo page" in {
      navigator.nextPage(EstateRegisteredOnlineYesNoPage, emptyUserAnswers)
        .mustBe(controllers.routes.HaveUTRYesNoController.onPageLoad())
    }

    "registered online" must {

      "HaveUTRYesNo page -> Yes with Registered online Yes -> Maintain an estate service" in {
        val answers = emptyUserAnswers
          .set(EstateRegisteredOnlineYesNoPage, true).success.value
          .set(HaveUTRYesNoPage, true).success.value

        navigator.nextPage(HaveUTRYesNoPage, answers).url mustBe appConfig.maintainAnEstateFrontendUrl

      }

      "HaveUTRYesNo page -> No with Registered online Yes -> UTR sent in post" in {
        val answers = emptyUserAnswers
          .set(EstateRegisteredOnlineYesNoPage, true).success.value
          .set(HaveUTRYesNoPage, false).success.value

        navigator.nextPage(HaveUTRYesNoPage, answers) mustBe controllers.routes.UTRSentInPostController.onPageLoad()
      }

    }

    "not registered online" must {

      "HaveUTRYesNo page -> Yes with Registered online No -> Must register online page" in {
        val answers = emptyUserAnswers
          .set(EstateRegisteredOnlineYesNoPage, false).success.value
          .set(HaveUTRYesNoPage, true).success.value

        navigator.nextPage(HaveUTRYesNoPage, answers)
          .mustBe(controllers.routes.MustRegisterEstateController.onPageLoad())
      }

      "HaveUTRYesNo page -> No with Registered online No -> Suitability questions" in {
        val answers = emptyUserAnswers
          .set(EstateRegisteredOnlineYesNoPage, false).success.value
          .set(HaveUTRYesNoPage, false).success.value

        navigator.nextPage(HaveUTRYesNoPage, answers).url mustBe appConfig.suitabilityUrl
      }
    }

  }
}
