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

import models.entities.Entity
import models.identification.Address
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class BusinessPersonalRep(name: String,
                                     phoneNumber: String,
                                     utr: Option[String],
                                     address: Address,
                                     email: Option[String])

object BusinessPersonalRep extends Entity {

  implicit val reads: Reads[BusinessPersonalRep] =
    ((__ \ Symbol("orgName")).read[String] and
      (__ \ Symbol("phoneNumber")).read[String] and
      __.lazyRead(readNullableAtSubPath[String](__ \ Symbol("identification") \ Symbol("utr"))) and
      __.lazyRead(readAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address"))) and
      (__ \ Symbol("email")).readNullable[String]).tupled.map {

      case (name, phoneNumber, utr, address, email) =>
        BusinessPersonalRep(name, phoneNumber, utr, address, email)
    }

  implicit val writes: Writes[BusinessPersonalRep] =
    ((__ \ Symbol("orgName")).write[String] and
      (__ \ Symbol("phoneNumber")).write[String] and
      (__ \ Symbol("identification") \ Symbol("utr")).writeNullable[String] and
      (__ \ Symbol("identification") \ Symbol("address")).write[Address] and
      (__ \ Symbol("email")).writeNullable[String]
      ).apply(unlift(BusinessPersonalRep.unapply))

}
