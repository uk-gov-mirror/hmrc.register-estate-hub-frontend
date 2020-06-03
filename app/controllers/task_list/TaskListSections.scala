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

package controllers.task_list

import config.FrontendAppConfig
import models.Tag.InProgress
import models.{CompletedTasks, Tag}
import viewmodels.tasks.{DeceasedPersons, EstateDetails, PersonalRep}
import viewmodels.{Link, Task}

trait TaskListSections {

  case class TaskList(mandatory: List[Task]) {
    val isAbleToDeclare : Boolean = !mandatory.exists(_.tag.contains(InProgress))
  }

  private lazy val notYetAvailable : String =
    controllers.routes.FeatureNotAvailableController.onPageLoad().url

  val config: FrontendAppConfig

  private def estateDetailsRouteEnabled(utr: String): String = {
    if (config.estateDetailsEnabled) {
      config.estateDetailsUrl(utr)
    } else {
      notYetAvailable
    }
  }

  private def personalRepRouteEnabled(utr: String): String = {
    if (config.personalRepEnabled) {
      config.personalRepUrl(utr)
    } else {
      notYetAvailable
    }
  }

  private def deceasedPersonsRouteEnabled(utr: String): String = {
    if (config.deceasedPersonsEnabled) {
      config.deceasedPersonsUrl(utr)
    } else {
      notYetAvailable
    }
  }

  def generateTaskList(tasks: CompletedTasks, utr: String) : TaskList = {
    val mandatorySections = List(
      Task(
        Link(EstateDetails, estateDetailsRouteEnabled(utr)),
        Some(Tag.tagFor(tasks.estateDetails, config.estateDetailsEnabled))
      ),
      Task(
        Link(PersonalRep, personalRepRouteEnabled(utr)),
        Some(Tag.tagFor(tasks.personalRep, config.personalRepEnabled))
      ),
      Task(
        Link(DeceasedPersons, deceasedPersonsRouteEnabled(utr)),
        Some(Tag.tagFor(tasks.deceasedPersons, config.deceasedPersonsEnabled))
      )
    )

    TaskList(mandatorySections)
  }

}
