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

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.CompletedTasks
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class EstatesStoreConnectorSpec extends SpecBase with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

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

  "estates store connector" must {

    "return OK with the current task status" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesStoreConnector]

      val json = Json.parse(
        """
          |{
          |  "details": true,
          |  "personalRepresentative": true,
          |  "deceased": true
          |}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates-store/register/tasks"))
          .willReturn(okJson(json.toString))
      )

      val result = connector.getStatusOfTasks

      result.futureValue mustBe
        CompletedTasks(details = true, personalRepresentative = true, deceased = true)

      application.stop()
    }

    "return default tasks when a failure occurs" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesStoreConnector]

      server.stubFor(
        get(urlEqualTo("/estates-store/register/tasks"))
          .willReturn(serverError())
      )

      val result = connector.getStatusOfTasks

      result.futureValue mustBe
        CompletedTasks()

      application.stop()
    }

  }

}
