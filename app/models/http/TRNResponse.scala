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

package models.http

import play.api.Logging
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import utils.Session

sealed trait DeclarationResponse

final case class TRNResponse(trn : String) extends DeclarationResponse

object TRNResponse {

  implicit val formats : Format[TRNResponse] = Json.format[TRNResponse]

}

object DeclarationResponse extends Logging {

  implicit object RegistrationResponseFormats extends Reads[DeclarationResponse] {

    override def reads(json: JsValue): JsResult[DeclarationResponse] = json.validate[TRNResponse]

  }

  case object AlreadyRegistered extends DeclarationResponse
  case object InternalServerError extends DeclarationResponse

  implicit def httpReads(implicit hc: HeaderCarrier): HttpReads[DeclarationResponse] =
    (method: String, url: String, response: HttpResponse) => {
      logger.info(s"[Session ID: ${Session.id(hc)}] response status received from estates api: ${response.status}")

      response.status match {
        case OK =>
          response.json.as[TRNResponse]
        case CONFLICT =>
          AlreadyRegistered
        case _ =>
          InternalServerError
      }
    }

}
