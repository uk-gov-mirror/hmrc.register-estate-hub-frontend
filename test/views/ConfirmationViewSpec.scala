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

import play.twirl.api.HtmlFormat
import utils.AccessibilityHelper._
import views.behaviours.ViewBehaviours
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewBehaviours {

  val refNumber = "XC TRN 00 00 00 49 11"
  val accessibleRefNumber: String = formatReferenceNumber(refNumber)

  val name = "John Smith"

  private def newTrust(view : HtmlFormat.Appendable) : Unit = {

    "assert content" in  {
      val doc = asDocument(view)

      assertContainsText(doc, "Registration received")
      assertContainsText(doc, "Your registration reference number is:")
      assertRenderedById(doc, "estates-registration-number")

      assertRenderedById(doc, "print-and-save")

      assertContainsText(doc, "We will post the estate’s Unique Taxpayer Reference (UTR) to . If they are based in the UK, this can take 15 working days. For an international personal representative, this can take up to 21 working days.")

      assertContainsText(doc, "Make a note of your registration number in case you need to contact HMRC. If the UTR does not arrive within 15 to 21 working days, the personal representative will need to")

      assertContainsText(doc, "You must keep the details for the personal representative up to date. This is the person that HMRC will contact and all official documents will be sent to. You can make changes to the personal representative’s details when you manage the estate.")

      assertContainsText(doc, "When you manage an estate you can also:")

      assertContainsText(doc, "You cannot update the details of the person who has died or the years of tax liability.")
    }

  }

  "Confirmation view" must {

    val messageKeyPrefix = "confirmation"

    val view = viewFor[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      trn = refNumber,
      personalRepName = ""
    )(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like newTrust(applyView)
  }
}
