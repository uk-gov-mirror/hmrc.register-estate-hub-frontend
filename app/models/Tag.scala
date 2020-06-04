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

package models

sealed trait Tag

object Tag extends Enumerable.Implicits {

  case object UpToDate extends WithName("up-to-date") with Tag

  case object InProgress extends WithName("in-progress") with Tag

  val values: Set[Tag] = Set(
    UpToDate, InProgress
  )

  implicit val enumerable: Enumerable[Tag] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  def tagFor(upToDate: Boolean, featureEnabled: Boolean) : Tag = {
    if (upToDate || !featureEnabled) {
      UpToDate
    } else {
      InProgress
    }
  }
}