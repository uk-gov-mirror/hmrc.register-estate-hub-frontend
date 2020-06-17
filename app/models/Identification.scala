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

import play.api.libs.functional.syntax._
import play.api.libs.json._

sealed trait IndividualIdentification

object IndividualIdentification {
  implicit val reads: Reads[IndividualIdentification] =
    (__ \ 'passport \ 'isPassport).read[Boolean].flatMap[IndividualIdentification] {
      case true => (__ \ 'passport).read[Passport].widen[IndividualIdentification]
      case false => (__ \ 'passport).read[IdCard].widen[IndividualIdentification]
    } orElse __.read[NationalInsuranceNumber].widen[IndividualIdentification]

  implicit val writes: Writes[IndividualIdentification] = Writes {
    case ni: NationalInsuranceNumber => Json.toJson(ni)(NationalInsuranceNumber.format)
    case p: Passport => Json.obj("passport" -> Json.toJson(p)(Passport.format))
    case i: IdCard => Json.obj("passport" -> Json.toJson(i)(IdCard.format))
  }
}

case class NationalInsuranceNumber(nino: String) extends IndividualIdentification

object NationalInsuranceNumber{
  implicit val format: Format[NationalInsuranceNumber] = Json.format[NationalInsuranceNumber]
}

case class Passport(countryOfIssue: String,
                    number: String,
                    expirationDate: LocalDate
                   ) extends IndividualIdentification

object Passport {

  implicit val reads: Reads[Passport] =
    ((__ \ 'countryOfIssue).read[String] and
      (__ \ 'number).read[String] and
      (__ \ 'expirationDate).read[LocalDate]).apply(Passport.apply _)

  implicit val writes: Writes[Passport] =
    ((__ \ 'countryOfIssue).write[String] and
      (__ \ 'number).write[String] and
      (__ \ 'expirationDate).write[LocalDate] and
      (__ \ 'isPassport).write[Boolean]
      ).apply(passport => (
      passport.countryOfIssue,
      passport.number,
      passport.expirationDate,
      true
    ))

  implicit val format: Format[Passport] = Format[Passport](reads, writes)
}

case class IdCard(countryOfIssue: String,
                  number: String,
                  expirationDate: LocalDate
                 ) extends IndividualIdentification

object IdCard {

  implicit val reads: Reads[IdCard] =
    ((__ \ 'countryOfIssue).read[String] and
      (__ \ 'number).read[String] and
      (__ \ 'expirationDate).read[LocalDate]).apply(IdCard.apply _)

  implicit val writes: Writes[IdCard] =
    ((__ \ 'countryOfIssue).write[String] and
      (__ \ 'number).write[String] and
      (__ \ 'expirationDate).write[LocalDate] and
      (__ \ 'isPassport).write[Boolean]
      ).apply(idCard => (
      idCard.countryOfIssue,
      idCard.number,
      idCard.expirationDate,
      false
    ))

  implicit val format: Format[IdCard] = Format[IdCard](reads, writes)
}
