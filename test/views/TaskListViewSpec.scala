/*
 * Copyright 2024 HM Revenue & Customs
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

import models.{CompletedTasks, TagStatus}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import viewmodels.tasks.{EstateName, PersonWhoDied, PersonalRepresentative, YearsOfTaxLiability}
import viewmodels.{Link, Task}
import views.behaviours.{TaskListViewBehaviours, ViewBehaviours}
import views.html.TaskListView

class TaskListViewSpec extends ViewBehaviours with TaskListViewBehaviours {

  private val estateName: Option[String] = Some("The estate of John Smith")
  private val url: String = "url"

  private def tasks(estateDetailsEnabled: Boolean,
            personalRepEnabled: Boolean,
            deceasedPersonsEnabled: Boolean,
            yearsOfTaxLiability: Boolean
           ): CompletedTasks =

    CompletedTasks(
      details = estateDetailsEnabled,
      personalRepresentative = personalRepEnabled,
      deceased = deceasedPersonsEnabled,
      yearsOfTaxLiability = yearsOfTaxLiability
    )

  private def sections(tasks: CompletedTasks): List[Task] = {
    List(
      Task(
        Link(EstateName, url),
        TagStatus.tagFor(tasks.details, featureEnabled = true)
      ),
      Task(
        Link(PersonalRepresentative, url),
        TagStatus.tagFor(tasks.personalRepresentative, featureEnabled = true)
      ),
      Task(
        Link(PersonWhoDied, url),
        TagStatus.tagFor(tasks.deceased, featureEnabled = true)
      ),
      Task(
        Link(YearsOfTaxLiability, url),
        TagStatus.tagForTaxLiability(tasks.yearsOfTaxLiability, featureEnabled = true, true, tasks.deceased )
      )
    )
  }

  "TaskList view" must {

    "render sections" must {

      val view = viewFor[TaskListView](Some(emptyUserAnswers))

      val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true, yearsOfTaxLiability = true )

      val tasksList = sections(completedTasks)

      val applyView = view.apply(estateName, tasksList, isTaskListComplete = true, Organisation)(fakeRequest, messages)

      behave like normalPage(applyView, "taskList")

      behave like pageWithBackLink(applyView)

      behave like taskList(applyView, tasksList)
    }

    "summary" must {

      val view = viewFor[TaskListView](Some(emptyUserAnswers))

      "be rendered" when {

        "all sections are completed" in {

          val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true, yearsOfTaxLiability = true)

          val tasksList = sections(completedTasks)

          val applyView = view.apply(estateName, tasksList, isTaskListComplete = true, Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertRenderedById(doc, "summary-heading")
          assertRenderedById(doc, "summary-paragraph")
          assertRenderedById(doc, "print-and-save")

        }
      }

      "not be rendered" when {

        "not all sections are completed" in {

          val completedTasks = tasks(estateDetailsEnabled = false, personalRepEnabled = false, deceasedPersonsEnabled = false, yearsOfTaxLiability = false)

          val tasksList = sections(completedTasks)

          val applyView = view.apply(estateName, tasksList, isTaskListComplete = false, Organisation)(fakeRequest, messages)
          val doc = asDocument(applyView)

          assertNotRenderedById(doc, "summary-heading")
          assertNotRenderedById(doc, "summary-paragraph")
          assertNotRenderedById(doc, "print-and-save")

        }
      }
    }

    "estate name" must {

      val view = viewFor[TaskListView](Some(emptyUserAnswers))

      val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true, yearsOfTaxLiability = true)

      val tasksList = sections(completedTasks)

      def applyView(estateName: Option[String]): HtmlFormat.Appendable =
        view.apply(estateName, tasksList, isTaskListComplete = true, Organisation)(fakeRequest, messages)

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

    "rendered for an Agent" must {

      "render Agent details link" in {
        val view = viewFor[TaskListView](Some(emptyUserAnswers))

        val completedTasks = tasks(estateDetailsEnabled = true, personalRepEnabled = true, deceasedPersonsEnabled = true, yearsOfTaxLiability = true)

        val tasksList = sections(completedTasks)

        val applyView = view.apply(estateName, tasksList, isTaskListComplete = true, Agent)(fakeRequest, messages)

        val doc = asDocument(applyView)
        assertRenderedById(doc, "agent-details")
      }

    }

  }
}
