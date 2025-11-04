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
import connectors.EstatesConnector
import forms.DeclarationFormProvider
import models.http.TRNResponse
import models.Declaration
import models.identification.Name
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.DeclarationPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.DeclarationView

import scala.concurrent.Future

class DeclarationControllerSpec extends SpecBase {

  private val formProvider = new DeclarationFormProvider()
  private val form: Form[Declaration] = formProvider()

  lazy val declarationRoute: String = routes.DeclarationController.onPageLoad().url

  "Declaration Controller" must {

    "return OK and the correct view for a GET for an Org" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, AffinityGroup.Organisation)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET for an Agent" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, AffinityGroup.Agent)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val declaration: Declaration = Declaration(Name("First", None, "Last"))

      val userAnswers = emptyUserAnswers
        .set(DeclarationPage, declaration).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, declarationRoute)

      val view = application.injector.instanceOf[DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(declaration), AffinityGroup.Organisation)(request, messages).toString

      application.stop()
    }

    "redirect to the confirmation page when valid data is submitted and registration submitted successfully " in {

      val mockConnector: EstatesConnector = mock[EstatesConnector]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(Seq(
          bind[EstatesConnector].to(mockConnector),
          bind[SessionRepository].toInstance(sessionRepository)
        ))
        .build()

      when(mockConnector.register(any())(any(), any())).thenReturn(Future.successful(TRNResponse("fakeTrn")))

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("firstName", "first"), ("lastName", "last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ConfirmationController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DeclarationView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, AffinityGroup.Organisation)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, declarationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, declarationRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
