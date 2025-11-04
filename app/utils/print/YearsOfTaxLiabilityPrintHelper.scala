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

import implicits.TaxYearImplicits
import models._
import play.api.i18n.Messages
import uk.gov.hmrc.time.TaxYear
import utils.YearFormatter
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class YearsOfTaxLiabilityPrintHelper @Inject()(yearFormatter: YearFormatter,
                                               taxYearImplicits: TaxYearImplicits,
                                               checkAnswersFormatters: CheckAnswersFormatters) {

  import taxYearImplicits._
  import yearFormatter._

  def apply(yearReturns: List[YearReturnType])(implicit messages: Messages): Seq[AnswerSection] = {

    def rows(taxYear: TaxYear): Seq[AnswerRow] =
      Seq(
        yesNoQuestion(value = true, "taxLiability.neededToPayTax", taxYear),
        yesNoQuestion(value = false, "taxLiability.wasTaxDeclared", taxYear)
      )

    yearReturns.zipWithIndex.foldLeft(Seq[AnswerSection]()) {
      case (acc, (yearReturn, index)) =>
        val taxYear: TaxYear = getTaxYear(yearReturn.taxReturnYear)
        acc :+ AnswerSection(
          headingKey = if (index == 0) Some("taskList.yearsOfTaxLiability.label") else None,
          rows = rows(taxYear),
          subHeading = Some(messages("taskList.yearOfTaxLiability.label", taxYear.start, taxYear.end))
        )
    }
  }

  private def getTaxYear(taxReturnYear: String): TaxYear = {
    val endYear: Int = taxReturnYear.fullYear
    TaxYear(startYear = endYear - 1)
  }

  private def yesNoQuestion(value: Boolean, labelKey: String, taxYear: TaxYear)(implicit messages: Messages): AnswerRow =
    AnswerRow(
      messages(s"$labelKey.checkYourAnswersLabel", taxYear.start, taxYear.end),
      checkAnswersFormatters.yesOrNo(value)
    )

}
