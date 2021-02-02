/*
 * Copyright 2021 HM Revenue & Customs
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

package models.entities.personalrep

import java.time.LocalDate

import models.entities.{Entity, personalrep}
import models.identification.{Address, IndividualIdentification, Name}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class IndividualPersonalRep(name: Name,
                                 dateOfBirth: LocalDate,
                                 identification: IndividualIdentification,
                                 address: Address,
                                 email: Option[String],
                                 phoneNumber: String)

object IndividualPersonalRep extends Entity {

  implicit val reads: Reads[IndividualPersonalRep] =
    ((__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).read[LocalDate] and
      __.lazyRead(readAtSubPath[IndividualIdentification](__ \ 'identification)) and
      __.lazyRead(readAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'email).readNullable[String] and
      (__ \ 'phoneNumber).read[String]).tupled.map{

      case (name, dob, identification, address, email, phoneNumber) =>
        personalrep.IndividualPersonalRep(name, dob, identification, address, email, phoneNumber)
    }

  implicit val writes: Writes[IndividualPersonalRep] =
    ((__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).write[LocalDate] and
      (__ \ 'identification).write[IndividualIdentification] and
      (__ \ 'identification \ 'address).write[Address] and
      (__ \ 'email).writeNullable[String] and
      (__ \ "phoneNumber").write[String]
      ).apply(unlift(IndividualPersonalRep.unapply))

}
