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
                                address : Option[Address])

object DeceasedPerson extends Entity {

  implicit val reads: Reads[DeceasedPerson] =
    ((__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).readNullable[LocalDate] and
      (__ \ 'dateOfDeath).read[LocalDate] and
      __.lazyRead(readNullableAtSubPath[NationalInsuranceNumber](__ \ 'identification)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address))).tupled.map{

      case (name, dob, dod, nino, identification) =>
        entities.DeceasedPerson(name, dob, dod, nino, identification)

    }

  implicit val writes: Writes[DeceasedPerson] =
    ((__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).writeNullable[LocalDate] and
      (__ \ 'dateOfDeath).write[LocalDate] and
      (__ \ 'identification).writeNullable[NationalInsuranceNumber] and
      (__ \ 'identification \ 'address).writeNullable[Address]
      ).apply(settlor => (
      settlor.name,
      settlor.dateOfBirth,
      settlor.dateOfDeath,
      settlor.nino,
      settlor.address
    ))
}
