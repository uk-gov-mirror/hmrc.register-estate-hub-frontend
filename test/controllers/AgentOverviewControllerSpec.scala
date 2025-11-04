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
import config.FrontendAppConfig
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.AgentOverviewView

class AgentOverviewControllerSpec extends SpecBase {

  lazy val agentOverviewRoute: String = routes.AgentOverviewController.onSubmit().url
  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  "AgentOverview Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, routes.AgentOverviewController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentOverviewView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "redirect for a POST" in {

      val application =
        applicationBuilder(userAnswers = None, AffinityGroup.Agent)
          .build()

      val request =
        FakeRequest(POST, agentOverviewRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.EstateRegisteredOnlineYesNoController.onPageLoad().url

      application.stop()

    }

  }
}
