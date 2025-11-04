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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models._
import models.entities.personalrep.PersonalRepresentativeType
import models.entities.{DeceasedPerson, EntitiesType}
import models.http.DeclarationResponse.{AlreadyRegistered, InternalServerError}
import models.http.TRNResponse
import models.identification.Name
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

class EstatesConnectorSpec extends SpecBase with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

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

  val declaration: Declaration = Declaration(
    name = Name("first", None, "last")
  )

  val registration: EstateRegistrationNoDeclaration = EstateRegistrationNoDeclaration(
    matchData = None,
    correspondence = CorrespondenceName(
      name = "name"
    ),
    yearsReturns = None,
    estate = Estate(
      entities = EntitiesType(PersonalRepresentativeType(), DeceasedPerson(Name("first", None, "last"), None, LocalDate.parse("1996-02-03"), None, None, None)),
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

      val result = connector.register(declaration)

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

      val result = connector.register(declaration)

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

      val result = connector.register(declaration)

      result.futureValue mustBe
        InternalServerError

      application.stop()
    }

    "return a personal representative name when the personal representative is an individual" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      val personalRepIndResponse = Json.parse(
        """
          |{
          |  "name": {
          |     "firstName": "Adam",
          |     "lastName": "Conder"
          |  },
          |  "dateOfBirth": "2010-10-10",
          |   "identification": {
          |     "nino": "JP121212A"
          |   },
          |   "phoneNumber": "+44 1911111"
          |}
          |""".stripMargin)

      val personalRepOrgResponse = Json.parse(
        """
          |{}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates/personal-rep/individual"))
          .willReturn(okJson(personalRepIndResponse.toString))
      )

      server.stubFor(
        get(urlEqualTo("/estates/personal-rep/organisation"))
          .willReturn(okJson(personalRepOrgResponse.toString))
      )

      val result = connector.getPersonalRepName()

      result.futureValue mustBe PersonalRepName("Adam Conder")

      application.stop()
    }

    "return a personal representative name when the personal representative is an organisation" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      val personalRepIndResponse = Json.parse(
        """
          |{}
          |""".stripMargin)

      val personalRepOrgResponse = Json.parse(
        """
          |{
          | "orgName": "Conder Ltd",
          | "phoneNumber": "+44 1911111",
          | "identification": {
          |   "utr": "1234567890"
          | }
          |}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates/personal-rep/individual"))
          .willReturn(okJson(personalRepIndResponse.toString))
      )

      server.stubFor(
        get(urlEqualTo("/estates/personal-rep/organisation"))
          .willReturn(okJson(personalRepOrgResponse.toString))
      )

      val result = connector.getPersonalRepName()

      result.futureValue mustBe PersonalRepName("Conder Ltd")

      application.stop()
    }

    "return an error when personal rep name cannot be retrieved" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesConnector]

      val personalRepIndResponse = Json.parse(
        """
          |{}
          |""".stripMargin)

      val personalRepOrgResponse = Json.parse(
        """
          |{}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates/personal-rep/individual"))
          .willReturn(okJson(personalRepIndResponse.toString))
      )

      server.stubFor(
        get(urlEqualTo("/estates/personal-rep/organisation"))
          .willReturn(okJson(personalRepOrgResponse.toString))
      )

      val result = connector.getPersonalRepName()

      result.failed.futureValue mustBe a[RuntimeException]

      application.stop()
    }

    "get registration request" when {

      val url: String = "/estates/registration"

      "successful return a registration document" in {

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
            | "correspondence": {
            |   "name": "name"
            | },
            | "estate": {
            |   "entities": {
            |     "personalRepresentative": {
            |     },
            |     "deceased": {
            |       "name": {
            |         "firstName": "first",
            |         "lastName": "last"
            |       },
            |       "dateOfDeath": "1996-02-03"
            |     }
            |   },
            |   "periodTaxDues": "periodTaxDues"
            | }
            |}
            |""".stripMargin)

        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(json.toString))
        )

        val result = connector.getRegistration()

        result.futureValue mustBe registration

        application.stop()

      }
    }

    "return a boolean to show if there is tax liability due" in {

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
          |true
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates/is-tax-required"))
          .willReturn(aResponse()
            .withStatus(Status.OK)
            .withBody(json.toString())
          )
      )

      val result = connector.getIsLiableForTax()

      result.futureValue mustBe
        true

      application.stop()
    }
  }

}
