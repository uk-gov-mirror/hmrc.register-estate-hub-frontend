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
import models.YearReturnType
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class YearsOfTaxLiabilityPrintHelperSpec extends SpecBase {

  private val helper: YearsOfTaxLiabilityPrintHelper = injector.instanceOf[YearsOfTaxLiabilityPrintHelper]

  "YearsOfTaxLiabilityPrintHelper" must {

    "render answer section" when {

      "estate has not needed to pay any tax" in {

        val result = helper(Nil)

        result mustBe Nil
      }

      "estate needed to pay tax" when {

        "one year" in {

          val yearReturns: List[YearReturnType] = List(
            YearReturnType("18", taxConsequence = true)
          )

          val result = helper(yearReturns)

          result mustBe Seq(
            AnswerSection(
              headingKey = Some("taskList.yearsOfTaxLiability.label"),
              rows = Seq(
                AnswerRow(
                  label = messages("taxLiability.neededToPayTax.checkYourAnswersLabel", "6 April 2017", "5 April 2018"),
                  answer = Html("Yes")
                ),
                AnswerRow(
                  label = messages("taxLiability.wasTaxDeclared.checkYourAnswersLabel", "6 April 2017", "5 April 2018"),
                  answer = Html("No")
                )
              ),
              subHeading = Some("Tax liability 6 April 2017 to 5 April 2018")
            )
          )
        }

        "more than one year" in {

          val yearReturns: List[YearReturnType] = List(
            YearReturnType("18", taxConsequence = true),
            YearReturnType("19", taxConsequence = true)
          )

          val result = helper(yearReturns)

          result mustBe Seq(
            AnswerSection(
              headingKey = Some("taskList.yearsOfTaxLiability.label"),
              rows = Seq(
                AnswerRow(
                  label = messages("taxLiability.neededToPayTax.checkYourAnswersLabel", "6 April 2017", "5 April 2018"),
                  answer = Html("Yes")
                ),
                AnswerRow(
                  label = messages("taxLiability.wasTaxDeclared.checkYourAnswersLabel", "6 April 2017", "5 April 2018"),
                  answer = Html("No")
                )
              ),
              subHeading = Some("Tax liability 6 April 2017 to 5 April 2018")
            ),
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(
                  label = messages("taxLiability.neededToPayTax.checkYourAnswersLabel", "6 April 2018", "5 April 2019"),
                  answer = Html("Yes")
                ),
                AnswerRow(
                  label = messages("taxLiability.wasTaxDeclared.checkYourAnswersLabel", "6 April 2018", "5 April 2019"),
                  answer = Html("No")
                )
              ),
              subHeading = Some("Tax liability 6 April 2018 to 5 April 2019")
            )
          )
        }
      }
    }
  }
}
