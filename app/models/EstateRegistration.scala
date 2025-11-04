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

package models

import java.time.LocalDate

import models.entities.EntitiesType
import models.identification.{Address, Name}
import play.api.libs.json.{Json, _}

case class EstateRegistration(matchData: Option[MatchData],
                              correspondence: Correspondence,
                              yearsReturns: Option[YearsReturns],
                              declaration: Declaration,
                              estate: Estate,
                              agentDetails: Option[AgentDetails] = None)

object EstateRegistration {
  implicit val estateRegistrationFormat : Format[EstateRegistration] = Json.format[EstateRegistration]
}

case class MatchData(utr: String,
                     name: String,
                     postCode: Option[String])

object MatchData {
  implicit val matchDataFormat: Format[MatchData] = Json.format[MatchData]
}

case class Correspondence(abroadIndicator: Boolean,
                          name: String,
                          address: Address,
                          phoneNumber: String)

object Correspondence {
  implicit val correspondenceFormat : Format[Correspondence] = Json.format[Correspondence]
}

case class YearsReturns(returns: List[YearReturnType])

object YearsReturns {
  implicit val yearsReturnsFormat: Format[YearsReturns] = Json.format[YearsReturns]
}

case class YearReturnType(taxReturnYear: String,
                          taxConsequence: Boolean)

object YearReturnType {
  implicit val yearReturnTypeFormat: Format[YearReturnType] = Json.format[YearReturnType]
}

case class Declaration(name: Name)

object Declaration {
  implicit lazy val formats: Format[Declaration] = Json.format[Declaration]
}

case class Estate(entities: EntitiesType,
                  administrationEndDate: Option[LocalDate],
                  periodTaxDues: String)

object Estate {
  implicit val estateFormat: Format[Estate] = Json.format[Estate]
}

case class AgentDetails(arn: String,
                        agentName: String,
                        agentAddress: Address,
                        agentTelephoneNumber: String,
                        clientReference: String)

object AgentDetails {
  implicit val agentDetailsFormat: Format[AgentDetails] = Json.format[AgentDetails]
}
