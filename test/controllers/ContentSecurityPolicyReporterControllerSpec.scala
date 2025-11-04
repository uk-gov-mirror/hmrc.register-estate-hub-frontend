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

package controllers

import base.SpecBase
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup

class ContentSecurityPolicyReporterControllerSpec extends SpecBase {

  /*
    https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP#testing_your_policy
  */

  "ContentSecurityPolicyReporter Controller" must {

    "report when there is a CSP violation" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(POST, routes.ContentSecurityPolicyReporterController.report().url)
        .withJsonBody(Json.parse(
          """
            |{
            |  "csp-report": {
            |    "document-uri": "http://example.com/signup.html",
            |    "referrer": "",
            |    "blocked-uri": "http://example.com/css/style.css",
            |    "violated-directive": "style-src cdn.example.com",
            |    "original-policy": "default-src 'none'; style-src cdn.example.com; report-uri /_/csp-reports"
            |  }
            |}
            |""".stripMargin))

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }
  }
}
