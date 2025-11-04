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
    ((__ \ Symbol("name")).read[Name] and
      (__ \ Symbol("dateOfBirth")).read[LocalDate] and
      __.lazyRead(readAtSubPath[IndividualIdentification](__ \ Symbol("identification"))) and
      __.lazyRead(readAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address"))) and
      (__ \ Symbol("email")).readNullable[String] and
      (__ \ Symbol("phoneNumber")).read[String]).tupled.map{

      case (name, dob, identification, address, email, phoneNumber) =>
        personalrep.IndividualPersonalRep(name, dob, identification, address, email, phoneNumber)
    }

  implicit val writes: Writes[IndividualPersonalRep] =
    ((__ \ Symbol("name")).write[Name] and
      (__ \ Symbol("dateOfBirth")).write[LocalDate] and
      (__ \ Symbol("identification")).write[IndividualIdentification] and
      (__ \ Symbol("identification") \ Symbol("address")).write[Address] and
      (__ \ Symbol("email")).writeNullable[String] and
      (__ \ "phoneNumber").write[String]
      ).apply(unlift(IndividualPersonalRep.unapply))

}
