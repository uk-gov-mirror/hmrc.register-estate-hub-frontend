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
import play.api.http.Status._
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import utils.Session

sealed trait CompletedTasksResponse

case class CompletedTasks(
                           details: Boolean,
                           personalRepresentative: Boolean,
                           deceased: Boolean,
                           yearsOfTaxLiability: Boolean
                         ) extends CompletedTasksResponse

object CompletedTasks {

  implicit val formats: Format[CompletedTasks] = Json.format[CompletedTasks]

  def apply() : CompletedTasks = CompletedTasks(
    details = false,
    personalRepresentative = false,
    deceased = false,
    yearsOfTaxLiability = false
  )
}

object CompletedTasksResponse extends Logging {

  case object InternalServerError extends CompletedTasksResponse

  implicit def httpReads(implicit hc: HeaderCarrier): HttpReads[CompletedTasksResponse] = (method: String, url: String, response: HttpResponse) => {
    logger.info(s"[Session ID: ${Session.id(hc)}] response status received from estates store api: ${response.status}")

    response.status match {
      case OK =>
        response.json.as[CompletedTasks]
      case _ =>
        InternalServerError
    }
  }
}
