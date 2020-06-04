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

package views

import models.{CompletedTasks, Tag}
import play.twirl.api.HtmlFormat
import viewmodels.tasks.{DeceasedPersons, EstateDetails, PersonalRep}
import viewmodels.{Link, Task}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val estateName: Option[String] = Some("The estate of John Smith")
  private val url: String = "url"

  private def tasks(estateDetailsEnabled: Boolean,
            personalRepEnabled: Boolean,
            deceasedPersonsEnabled: Boolean
           ): CompletedTasks =

    CompletedTasks(
      estateDetails = estateDetailsEnabled,
      personalRep = personalRepEnabled,
      deceasedPersons = deceasedPersonsEnabled
    )

  private def sections(tasks: CompletedTasks): List[Task] = {
    List(
      Task(
        Link(EstateDetails, url),
        Some(Tag.tagFor(tasks.estateDetails, featureEnabled = true))
      ),
      Task(
        Link(PersonalRep, url),
        Some(Tag.tagFor(tasks.personalRep, featureEnabled = true))
      ),
      Task(
        Link(DeceasedPersons, url),
        Some(Tag.tagFor(tasks.deceasedPersons, featureEnabled = true))
      )
    )
  }

  "TaskList view" must {

    "render sections" must {

      val view = viewFor[TaskListView](Some(emptyUserAnswers))

      val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true)

      val tasksList = sections(completedTasks)

      val applyView = view.apply(estateName, tasksList, isTaskListComplete = true)(fakeRequest, messages)

      behave like normalPage(applyView, "taskList")

      behave like pageWithBackLink(applyView)

      behave like taskList(applyView, tasksList)
    }

    "summary" must {

      val view = viewFor[TaskListView](Some(emptyUserAnswers))

      "be rendered" when {

        "all sections are completed" in {

          val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true)

          val tasksList = sections(completedTasks)

          val applyView = view.apply(estateName, tasksList, isTaskListComplete = true)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertRenderedById(doc, "summary-heading")
          assertRenderedById(doc, "summary-paragraph")
          assertRenderedById(doc, "print-and-save")

        }
      }

      "not be rendered" when {

        "not all sections are completed" in {

          val completedTasks = tasks(estateDetailsEnabled = false, personalRepEnabled = false, deceasedPersonsEnabled = false)

          val tasksList = sections(completedTasks)

          val applyView = view.apply(estateName, tasksList, isTaskListComplete = false)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertNotRenderedById(doc, "summary-heading")
          assertNotRenderedById(doc, "summary-paragraph")
          assertNotRenderedById(doc, "print-and-save")

        }
      }
    }

    "estate name" must {

      val view = viewFor[TaskListView](Some(emptyUserAnswers))

      val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true)

      val tasksList = sections(completedTasks)

      def applyView(estateName: Option[String]): HtmlFormat.Appendable =
        view.apply(estateName, tasksList, isTaskListComplete = true)(fakeRequest, messages)

      "be rendered" when {

        "user has entered name in estate details" in {

          val doc = asDocument(applyView(estateName))
          assertRenderedById(doc, "estate-name")

        }
      }

      "not be rendered" when {

        "user has not entered name in estate details" in {

          val doc = asDocument(applyView(None))
          assertNotRenderedById(doc, "estate-name")

        }
      }
    }
    
  }
}
