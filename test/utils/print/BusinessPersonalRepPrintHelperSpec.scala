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

package utils.print

import base.SpecBase
import models.entities.personalrep
import models.entities.personalrep.BusinessPersonalRep
import models.identification.{NonUkAddress, UkAddress}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class BusinessPersonalRepPrintHelperSpec extends SpecBase {

  val helper: BusinessPersonalRepPrintHelper = injector.instanceOf[BusinessPersonalRepPrintHelper]

  val name: String = "Business Ltd."
  val utr: Option[String] = Some("1234567890")
  val ukAddress: UkAddress = UkAddress("21 Test Lane", "Testville", None, None, "NE1 1NE")
  val nonUkAddress: NonUkAddress = NonUkAddress("99 Test Lane", "Testville", None, "DE")
  val email: Option[String] = Some("email@example.com")
  val phoneNumber: String = "+447123456789"

  "BusinessPersonalRepPrintHelper" must {

    "render answer section for business personal rep with a UK address, no UTR and an email address" in {

      val personalRep: BusinessPersonalRep = personalrep.BusinessPersonalRep(
        name,
        phoneNumber,
        None,
        ukAddress,
        email
      )

      val result = helper(personalRep)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personalRepresentative.label"),
        rows = Seq(
          AnswerRow(label = messages("personalRep.individualOrBusiness.checkYourAnswersLabel"), answer = Html("Business"), None),
          AnswerRow(label = messages("personalRep.business.ukRegisteredYesNo.checkYourAnswersLabel"), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.business.name.checkYourAnswersLabel"), answer = Html("Business Ltd."), None),
          AnswerRow(label = messages("personalRep.business.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.business.address.checkYourAnswersLabel", name), answer = Html("21 Test Lane<br />Testville<br />NE1 1NE"), None),
          AnswerRow(label = messages("personalRep.business.emailYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.business.email.checkYourAnswersLabel", name), answer = Html("email@example.com"), None),
          AnswerRow(label = messages("personalRep.business.telephoneNumber.checkYourAnswersLabel", name), answer = Html("+447123456789"), None)
        )
      )
    }

    "render answer section for business personal rep with a non-UK address and a UTR" in {

      val personalRep: BusinessPersonalRep = personalrep.BusinessPersonalRep(
        name,
        phoneNumber,
        utr,
        nonUkAddress,
        None
      )

      val result = helper(personalRep)

      result mustBe AnswerSection(
        headingKey = Some("taskList.personalRepresentative.label"),
        rows = Seq(
          AnswerRow(label = messages("personalRep.individualOrBusiness.checkYourAnswersLabel"), answer = Html("Business"), None),
          AnswerRow(label = messages("personalRep.business.ukRegisteredYesNo.checkYourAnswersLabel"), answer = Html("Yes"), None),
          AnswerRow(label = messages("personalRep.business.name.checkYourAnswersLabel"), answer = Html("Business Ltd."), None),
          AnswerRow(label = messages("personalRep.business.utr.checkYourAnswersLabel", name), answer = Html("1234567890"), None),
          AnswerRow(label = messages("personalRep.business.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.business.address.checkYourAnswersLabel", name), answer = Html("99 Test Lane<br />Testville<br />Germany"), None),
          AnswerRow(label = messages("personalRep.business.emailYesNo.checkYourAnswersLabel", name), answer = Html("No"), None),
          AnswerRow(label = messages("personalRep.business.telephoneNumber.checkYourAnswersLabel", name), answer = Html("+447123456789"), None)
        )
      )
    }
  }
}
