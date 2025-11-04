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

package utils

import java.time.{LocalDate, LocalDateTime}
import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}
import uk.gov.hmrc.play.language.LanguageUtils

class DateFormatterSpec extends SpecBase {

  val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]
  val englishMessages: MessagesImpl = MessagesImpl(Lang("en"), messagesApi)
  val welshMessages: MessagesImpl = MessagesImpl(Lang("cy"), messagesApi)

  "Date Formatter" must {

    "format date" when {
      val dateToFormat = LocalDate.of(2020, 4, 6)

      "language set to English" in {
        val result = new DateFormatter(languageUtils).formatDate(dateToFormat)(englishMessages)
        result mustEqual "6 April 2020"
      }

      "language set to Welsh" in {
        val result = new DateFormatter(languageUtils).formatDate(dateToFormat)(welshMessages)
        result mustEqual "6 Ebrill 2020"
      }
    }

    "format date time" when {
      val dateTimeToFormat = LocalDateTime.of(2020, 4, 6, 0, 0, 0)

      "language set to English" in {
        val result = new DateFormatter(languageUtils).formatDate(dateTimeToFormat)(englishMessages)
        result mustEqual "6 April 2020"
      }

      "language set to Welsh" in {
        val result = new DateFormatter(languageUtils).formatDate(dateTimeToFormat)(welshMessages)
        result mustEqual "6 Ebrill 2020"
      }
    }
  }

}
