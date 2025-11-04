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

import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

case class PersonalRepName(name: String)

object PersonalRepName extends Logging {

  implicit val formats : Format[PersonalRepName] = Json.format[PersonalRepName]

  val personalRepIndividualNameReads : HttpReads[Option[PersonalRepName]] = (method: String, url: String, response: HttpResponse) => {
    response.status match {
      case OK =>
        val firstName = (response.json \ "name" \ "firstName").asOpt[String]
        val lastName = (response.json \ "name" \ "lastName").asOpt[String]
        (firstName, lastName) match {
          case (Some(f), Some(l)) => Some(PersonalRepName(s"$f $l"))
          case _ => None
        }
      case status =>
        logger.error(s"Error response from estates $status body: ${response.body}")
        None
    }
  }

  val personalRepOrganisationNameReads : HttpReads[Option[PersonalRepName]] = (method: String, url: String, response: HttpResponse) => {
    response.status match {
      case OK =>
        val orgName = (response.json \ "orgName").asOpt[String]
        orgName.map(x => PersonalRepName(x))
      case status =>
        logger.error(s"Error response from estates $status body: ${response.body}")
        None
    }
  }

}
