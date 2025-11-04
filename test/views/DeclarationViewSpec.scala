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

package views

import forms.DeclarationFormProvider
import models.Declaration
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup
import views.behaviours.QuestionViewBehaviours
import views.html.DeclarationView

class DeclarationViewSpec extends QuestionViewBehaviours[Declaration] {

  val messageKeyPrefix = "declaration"

  val form = new DeclarationFormProvider()()

  "Declaration view for Org or Agent" must {

    val view = viewFor[DeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, AffinityGroup.Organisation)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      None,
      controllers.routes.ConfirmationController.onPageLoad().url,
      "firstName", "middleName", "lastName"
    )
  }

  "render declaration warning for an Org" in {
    val view = viewFor[DeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, AffinityGroup.Organisation)(fakeRequest, messages)

    val doc = asDocument(applyView(form))
    assertContainsText(doc, "I confirm that the information I have given is true and complete to the best of my knowledge. I will make sure it is kept up to date, including any change of address. If I find out that I have made an error or something has changed, I will update the information.")
  }

  "render declaration warning for an Agent" in {
    val view = viewFor[DeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, AffinityGroup.Agent)(fakeRequest, messages)

    val doc = asDocument(applyView(form))
    assertContainsText(doc, "I confirm that the information my client has given is true and complete to the best of their knowledge. I will make sure it is kept up to date, including any change of address. If I find out that an error has been made or something has changed, I will update the information.")
  }

}
