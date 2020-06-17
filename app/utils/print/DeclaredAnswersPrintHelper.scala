/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

import javax.inject.Inject
import models._
import play.api.i18n.Messages
import utils.countryOptions.AllCountryOptions
import viewmodels.AnswerSection

class DeclaredAnswersPrintHelper @Inject()(countryOptions: AllCountryOptions,
                                           individualPersonalRepPrintHelper: IndividualPersonalRepPrintHelper,
                                           businessPersonalRepPrintHelper: BusinessPersonalRepPrintHelper,
                                           deceasedPersonPrintHelper: DeceasedPersonPrintHelper) {

  // TODO - get the estate from the backend

  val estate: EstateRegistration = EstateRegistration(
    matchData = None,
    correspondence = Correspondence(
      abroadIndicator = false,
      name = "name",
      address = UkAddress("line1", "line2", None, None, "NE22NE"),
      phoneNumber = "123"
    ),
    yearsReturns = None,
    declaration = Declaration(
      name = Name("first", None, "last")
    ),
    estate = Estate(
      entities = EntitiesType(
        PersonalRepresentativeType(
          Some(IndividualPersonalRep(
            Name("first", None, "last"),
            LocalDate.parse("1996-02-03"),
            NationalInsuranceNumber("AA000000A"),
            UkAddress("line1", "line2", None, None, "NE11NE"),
            "999"
          )),
          None
        ),
        DeceasedPerson(
          Name("first", None, "last"),
          Some(LocalDate.parse("1996-02-03")),
          LocalDate.parse("2020-02-03"),
          nino = Some(NationalInsuranceNumber("BB000000B")),
          None
        )
      ),
      administrationEndDate = None,
      periodTaxDues = "periodTaxDues"
    ),
    agentDetails = None
  )

  def entities()(implicit messages: Messages): Seq[AnswerSection] = {

    List(
      estateDetails(estate.correspondence.name),
      personalRep(estate.estate.entities.personalRepresentative),
      deceasedPersonPrintHelper(estate.estate.entities.deceased)
    )

  }

  private def estateDetails(name: String)(implicit messages: Messages): AnswerSection = {
    val converter = AnswerRowConverter(countryOptions)
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
}
