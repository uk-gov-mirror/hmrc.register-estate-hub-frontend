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

import java.time.LocalDate
import base.SpecBase
import org.mockito.Mockito.when
import services.LocalDateService

class YearFormatterSpec extends SpecBase {

  "Year formatter" when {

    "current year is 2019" must {

      val mockLocalDateService = mock[LocalDateService]
      val date = LocalDate.parse("2019-02-03")
      when(mockLocalDateService.now).thenReturn(date)
      val formatter = new YearFormatter(mockLocalDateService)
      import formatter._

      "format 16 as 2016" in {
        val input = "16"
        input.fullYear mustBe 2016
      }

      "format 17 as 2017" in {
        val input = "17"
        input.fullYear mustBe 2017
      }

      "format 18 as 2018" in {
        val input = "18"
        input.fullYear mustBe 2018
      }

      "format 19 as 2019" in {
        val input = "19"
        input.fullYear mustBe 2019
      }
    }

    "current year is 2119" must {

      val mockLocalDateService = mock[LocalDateService]
      val date = LocalDate.parse("2119-02-03")
      when(mockLocalDateService.now).thenReturn(date)
      val formatter = new YearFormatter(mockLocalDateService)
      import formatter._

      "format 16 as 2116" in {
        val input = "16"
        input.fullYear mustBe 2116
      }

      "format 17 as 2117" in {
        val input = "17"
        input.fullYear mustBe 2117
      }

      "format 18 as 2118" in {
        val input = "18"
        input.fullYear mustBe 2118
      }

      "format 19 as 2119" in {
        val input = "19"
        input.fullYear mustBe 2119
      }
    }

    "current year is 2101" must {

      val mockLocalDateService = mock[LocalDateService]
      val date = LocalDate.parse("2101-02-03")
      when(mockLocalDateService.now).thenReturn(date)
      val formatter = new YearFormatter(mockLocalDateService)
      import formatter._

      "format 98 as 2098" in {
        val input = "98"
        input.fullYear mustBe 2098
      }

      "format 99 as 2099" in {
        val input = "99"
        input.fullYear mustBe 2099
      }

      "format 00 as 2100" in {
        val input = "00"
        input.fullYear mustBe 2100
      }

      "format 01 as 2101" in {
        val input = "01"
        input.fullYear mustBe 2101
      }
    }
  }
}
