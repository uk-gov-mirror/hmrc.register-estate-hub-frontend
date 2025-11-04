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

import models._
import models.entities.personalrep.PersonalRepresentativeType
import play.api.i18n.Messages
import viewmodels.AnswerSection

import javax.inject.Inject

class RegistrationAnswersPrintHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters,
                                               individualPersonalRepPrintHelper: IndividualPersonalRepPrintHelper,
                                               businessPersonalRepPrintHelper: BusinessPersonalRepPrintHelper,
                                               deceasedPersonPrintHelper: DeceasedPersonPrintHelper,
                                               yearsOfTaxLiabilityPrintHelper: YearsOfTaxLiabilityPrintHelper) {

  def apply(registration: EstateRegistrationNoDeclaration)(implicit messages: Messages): Seq[AnswerSection] = {

    Seq(
      estateDetails(registration.correspondence.name),
      personalRep(registration.estate.entities.personalRepresentative),
      deceasedPersonPrintHelper(registration.estate.entities.deceased)
    ) ++ yearsOfTaxLiability(registration.yearsReturns)

  }

  private def estateDetails(name: String)(implicit messages: Messages): AnswerSection = {
    val converter = AnswerRowConverter()(checkAnswersFormatters)
    AnswerSection(
      Some("taskList.estateName.label"),
      Seq(
        converter.stringQuestion(name, "estateDetails.name")
      )
    )
  }

  private def personalRep(personalRep: PersonalRepresentativeType)(implicit messages: Messages): AnswerSection = {
    personalRep match {
      case PersonalRepresentativeType(Some(individual), None) => individualPersonalRepPrintHelper(individual)
      case PersonalRepresentativeType(None, Some(business)) => businessPersonalRepPrintHelper(business)
      case _ => AnswerSection(None, Nil)
    }
  }

  private def yearsOfTaxLiability(yearsReturns: Option[YearsReturns])(implicit messages: Messages): Seq[AnswerSection] = {
    yearsReturns match {
      case Some(yearsReturns) => yearsOfTaxLiabilityPrintHelper(yearsReturns.returns)
      case _ => Nil
    }
  }
}
