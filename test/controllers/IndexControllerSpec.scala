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

import base.SpecBase
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {


    "redirect to AgentOverview with Agent affinityGroup for a GET" in {
      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent)
        .overrides(Seq(
          bind[SessionRepository].toInstance(sessionRepository)
        ))
        .build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.AgentOverviewController.onPageLoad().url

      application.stop()
    }

    "redirect to EstateRegisteredOnlineYesNoController" in {
      val application = applicationBuilder(userAnswers = None)
        .overrides(Seq(
          bind[SessionRepository].toInstance(sessionRepository)
        ))
        .build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.EstateRegisteredOnlineYesNoController.onPageLoad().url

      application.stop()
    }
  }
}
