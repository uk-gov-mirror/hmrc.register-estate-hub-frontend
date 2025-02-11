/*
 * Copyright 2021 HM Revenue & Customs
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

package implicits

import javax.inject.Inject
import org.joda.time.LocalDate
import uk.gov.hmrc.time.TaxYear
import utils.DateFormatter

class TaxYearImplicits @Inject()(dateFormatter: DateFormatter) {

  implicit class TaxYearWithFormattedDates(taxYear: TaxYear) {
    private def formatDate(date: LocalDate): String = dateFormatter.formatDate(date)
    val start: String = formatDate(taxYear.starts)
    val end: String = formatDate(taxYear.finishes)
  }
}
