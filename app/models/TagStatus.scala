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

sealed trait TagStatus

object TagStatus extends Enumerable.Implicits {

  case object Completed extends WithName("completed") with TagStatus

  case object Incomplete extends WithName("incomplete") with TagStatus

  case object CannotStartYet extends WithName("cannot-start-yet") with TagStatus

  case object NoActionNeeded extends WithName("no-action-needed") with TagStatus


  val values: Set[TagStatus] = Set(
    Completed, Incomplete
  )

  implicit val enumerable: Enumerable[TagStatus] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  def tagFor(upToDate: Boolean, featureEnabled: Boolean) : TagStatus = {
    if (upToDate || !featureEnabled ) {
      Completed
    } else{
      Incomplete
    }
  }

  def tagForTaxLiability(upToDate: Boolean, featureEnabled: Boolean, enableTaxLiability : Boolean, isPreviousCompleted : Boolean) : TagStatus = {
    if (upToDate || !featureEnabled ) {
      Completed
    } else if( isPreviousCompleted && !enableTaxLiability){
      NoActionNeeded
    } else if(isPreviousCompleted && enableTaxLiability){
      Incomplete
    }else{
      CannotStartYet
    }
  }
}


