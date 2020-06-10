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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.http.DeclarationResponse.{AlreadyRegistered, InternalServerError}
import models.http.TRNResponse
import models.{AddressType, Correspondence, Declaration, EntitiesType, Estate, EstateRegistration, EstateWillType, Name, PersonalRepresentativeType}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class EstatesConnectorSpec extends SpecBase with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit def executionContext: ExecutionContext = injector.instanceOf[ExecutionContext]

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  val registration: EstateRegistration = EstateRegistration(
    matchData = None,
    correspondence = Correspondence(
      abroadIndicator = false,
      name = "name",
      address = AddressType("line1", "line2", None, None, None, "GB"),
      phoneNumber = "tel"
    ),
    yearsReturns = None,
    declaration = Declaration(
      name = Name("first", None, "last")
    ),
    estate = Estate(
      entities = EntitiesType(PersonalRepresentativeType(), EstateWillType(Name("first", None, "last"), None, LocalDate.parse("1996-02-03"), None)),
      administrationEndDate = None,
      periodTaxDues = "periodTaxDues"
    ),
    agentDetails = None
  )

  "estates connector" must {

    "return a registration number" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      val json = Json.parse(
        """
          |{
          |  "trn": "XTRN1234567"
          |}
          |""".stripMargin)

      server.stubFor(
        post(urlEqualTo("/estates/register"))
          .willReturn(okJson(json.toString))
      )

      val result = connector.register(registration)

      result.futureValue mustBe
        TRNResponse("XTRN1234567")

      application.stop()
    }

    "return already registered response when there is a conflict" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      val json = Json.parse(
        """
          |{
          | "code": "ALREADY_REGISTERED",
          | "message": "Estate already registered."
          |}
          |""".stripMargin)

      server.stubFor(
        post(urlEqualTo("/estates/register"))
          .willReturn(aResponse()
            .withStatus(Status.CONFLICT)
            .withBody(json.toString())
          )
      )

      val result = connector.register(registration)

      result.futureValue mustBe
        AlreadyRegistered

      application.stop()
    }

    "return internal server error response when there is an error" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      val json = Json.parse(
        """
          |{
          | "code": "INTERNAL_SERVER_ERROR",
          | "message": "Internal server error."
          |}
          |""".stripMargin)

      server.stubFor(
        post(urlEqualTo("/estates/register"))
          .willReturn(aResponse()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withBody(json.toString())
          )
      )

      val result = connector.register(registration)

      result.futureValue mustBe
        InternalServerError

      application.stop()
    }

  }

}
