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

package models

import java.time.LocalDate

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
                          address: AddressType,
                          phoneNumber: String)

object Correspondence {
  implicit val correspondenceFormat : Format[Correspondence] = Json.format[Correspondence]

}

case class YearsReturns(var returns: Option[List[YearReturnType]])

object YearsReturns {
  implicit val yearsReturnsFormat: Format[YearsReturns] = Json.format[YearsReturns]
}

case class YearReturnType(taxReturnYear: String,
                          taxConsequence: Boolean)

object YearReturnType {
  implicit val yearReturnTypeFormat: Format[YearReturnType] = Json.format[YearReturnType]
}

case class Estate(entities: EntitiesType,
                  administrationEndDate: Option[LocalDate],
                  periodTaxDues: String)

object Estate {
  implicit val estateFormat: Format[Estate] = Json.format[Estate]
}

case class EntitiesType(personalRepresentative: PersonalRepresentativeType,
                        deceased: EstateWillType)

object EntitiesType {
  implicit val entitiesTypeFormat: Format[EntitiesType] = Json.format[EntitiesType]
}


case class PersonalRepresentativeType (estatePerRepInd : Option[EstatePerRepIndType] = None,
                                       estatePerRepOrg : Option[EstatePerRepOrgType] = None)

object PersonalRepresentativeType {
  implicit val personalRepresentativeTypeFormat: Format[PersonalRepresentativeType] = Json.format[PersonalRepresentativeType]
}

case class EstatePerRepIndType(name: NameType,
                               dateOfBirth: LocalDate,
                               identification: IdentificationType,
                               phoneNumber: String,
                               email: Option[String])

object EstatePerRepIndType {
  implicit val estatePerRepIndTypeFormat: Format[EstatePerRepIndType] = Json.format[EstatePerRepIndType]
}

case class EstatePerRepOrgType(orgName: String,
                               phoneNumber: String,
                               email: Option[String] = None,
                               identification: IdentificationOrgType)

object EstatePerRepOrgType {
  implicit val estatePerRepOrgTypeFormat: Format[EstatePerRepOrgType] = Json.format[EstatePerRepOrgType]
}


case class EstateWillType(name: NameType,
                          dateOfBirth: Option[LocalDate],
                          dateOfDeath: LocalDate,
                          identification: Option[IdentificationType])

object EstateWillType {
  implicit val estateWillTypeFormat: Format[EstateWillType] = Json.format[EstateWillType]
}

case class AgentDetails(arn: String,
                        agentName: String,
                        agentAddress: AddressType,
                        agentTelephoneNumber: String,
                        clientReference: String)
object AgentDetails {
  implicit val agentDetailsFormat: Format[AgentDetails] = Json.format[AgentDetails]
}

case class NameType(firstName: String,
                    middleName: Option[String],
                    lastName: String)

object NameType {
  implicit val nameTypeFormat: Format[NameType] = Json.format[NameType]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String)

object AddressType {
  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}

case class IdentificationType(nino: Option[String],
                              passport: Option[PassportType],
                              address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class IdentificationOrgType(utr: Option[String],
                                 address: Option[AddressType])

object IdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class PassportType(number: String,
                        expirationDate: LocalDate,
                        countryOfIssue: String)

object PassportType {
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}
