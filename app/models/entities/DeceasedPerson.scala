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

package models.entities

import java.time.LocalDate

import models.identification.{Address, Name, NationalInsuranceNumber}
import models.entities
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class DeceasedPerson(name: Name,
                                dateOfBirth: Option[LocalDate],
                                dateOfDeath: LocalDate,
                                nino: Option[NationalInsuranceNumber],
                                addressYesNo: Option[Boolean],
                                address : Option[Address])

object DeceasedPerson extends Entity {

  implicit val reads: Reads[DeceasedPerson] =
    ((__ \ Symbol("name")).read[Name] and
      (__ \ Symbol("dateOfBirth")).readNullable[LocalDate] and
      (__ \ Symbol("dateOfDeath")).read[LocalDate] and
      __.lazyRead(readNullableAtSubPath[NationalInsuranceNumber](__ \ Symbol("identification"))) and
      (__ \ Symbol("addressYesNo")).readNullable[Boolean] and
      __.lazyRead(readNullableAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address")))).tupled.map{

      case (name, dob, dod, nino, addressYesNo, identification) =>
        entities.DeceasedPerson(name, dob, dod, nino, addressYesNo, identification)

    }

  implicit val writes: Writes[DeceasedPerson] =
    ((__ \ Symbol("name")).write[Name] and
      (__ \ Symbol("dateOfBirth")).writeNullable[LocalDate] and
      (__ \ Symbol("dateOfDeath")).write[LocalDate] and
      (__ \ Symbol("identification")).writeNullable[NationalInsuranceNumber] and
      (__ \ Symbol("addressYesNo")).writeNullable[Boolean] and
      (__ \ Symbol("identification") \ Symbol("address")).writeNullable[Address]
      ).apply(settlor => (
      settlor.name,
      settlor.dateOfBirth,
      settlor.dateOfDeath,
      settlor.nino,
      settlor.addressYesNo,
      settlor.address
    ))
}
