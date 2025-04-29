/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.EstatesConnector
import models.PersonalRepName
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.TRNPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmationView

import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBase {

  val mockConnector: EstatesConnector = mock[EstatesConnector]

  "Confirmation Controller" must {

    "return OK and the correct view for a GET when TRN is available" in {

      when(mockConnector.getPersonalRepName()(any(), any())).thenReturn(Future.successful(PersonalRepName("Adam")))

      val trn: String = "XC TRN 000 000 4911"

      val userAnswers = emptyUserAnswers.set(TRNPage, trn).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[EstatesConnector].to(mockConnector)
        )
        .build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(trn, "Adam")(request, messages).toString

      application.stop()

    }

    "return InternalServerError when TRN is not available" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }

  }
}
