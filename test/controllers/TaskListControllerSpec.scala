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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import connectors.{EstatesConnector, EstatesStoreConnector}
import models.CompletedTasks
import models.CompletedTasksResponse.InternalServerError
import models.Tag._
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewmodels.tasks.{DeceasedPersons, EstateDetails, PersonalRep}
import viewmodels.{Link, Task}
import views.html.TaskListView

import scala.concurrent.Future

class TaskListControllerSpec extends SpecBase {

  private val estateDetailsRoute: String = "http://localhost:8823/register-an-estate/details"
  private val personalRepRoute: String = "http://localhost:8825/register-an-estate/personal-representative"
  private val deceasedPersonsRoute: String = "http://localhost:8824/register-an-estate/deceased-person"
  private val featureUnavailableRoute: String = "http://localhost:8822/register-an-estate/feature-not-available"

  private val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  private val mockRepository : SessionRepository = mock[SessionRepository]

  when(mockAppConfig.estateDetailsFrontendUrl) thenReturn estateDetailsRoute
  when(mockAppConfig.personalRepFrontendUrl) thenReturn personalRepRoute
  when(mockAppConfig.deceasedPersonsFrontendUrl) thenReturn deceasedPersonsRoute
  when(mockAppConfig.featureUnavailableUrl) thenReturn featureUnavailableRoute
  when(mockAppConfig.analyticsToken) thenReturn "N/A"

  private val mockEstatesStoreConnector: EstatesStoreConnector = mock[EstatesStoreConnector]
  private val mockEstatesConnector: EstatesConnector = mock[EstatesConnector]

  "TaskList Controller" must {

    "return OK and the correct view for a GET" when {

      "all routes are enabled" in {

        val sections = List(
          Task(Link(EstateDetails, estateDetailsRoute), Some(Completed)),
          Task(Link(PersonalRep, personalRepRoute), Some(Completed)),
          Task(Link(DeceasedPersons, deceasedPersonsRoute), Some(Completed))
        )

        when(mockRepository.set(any())).thenReturn(Future.successful(true))

        when(mockEstatesStoreConnector.getStatusOfTasks(any(), any()))
          .thenReturn(Future.successful(CompletedTasks(details = true, personalRepresentative = true, deceased = true)))

        when(mockEstatesConnector.getEstateName()(any(), any())).thenReturn(Future.successful(None))

        when(mockAppConfig.estateDetailsEnabled) thenReturn true
        when(mockAppConfig.personalRepEnabled) thenReturn true
        when(mockAppConfig.deceasedPersonsEnabled) thenReturn true

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(Seq(
            bind(classOf[EstatesStoreConnector]).toInstance(mockEstatesStoreConnector),
            bind(classOf[EstatesConnector]).toInstance(mockEstatesConnector),
            bind[FrontendAppConfig].toInstance(mockAppConfig),
            bind[SessionRepository].toInstance(mockRepository)
          ))
          .build()

        val request = FakeRequest(GET, controllers.registration_progress.routes.TaskListController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(None, sections, isTaskListComplete = true)(fakeRequest, messages).toString

        application.stop()
      }

      "no routes enabled" in {

        val sections = List(
          Task(Link(EstateDetails, featureUnavailableRoute), Some(Completed)),
          Task(Link(PersonalRep, featureUnavailableRoute), Some(Completed)),
          Task(Link(DeceasedPersons, featureUnavailableRoute), Some(Completed))
        )

        when(mockRepository.set(any())).thenReturn(Future.successful(true))

        when(mockEstatesStoreConnector.getStatusOfTasks(any(), any()))
          .thenReturn(Future.successful(CompletedTasks(details = true, personalRepresentative = true, deceased = true)))

        when(mockEstatesConnector.getEstateName()(any(), any())).thenReturn(Future.successful(None))

        when(mockAppConfig.estateDetailsEnabled) thenReturn false
        when(mockAppConfig.personalRepEnabled) thenReturn false
        when(mockAppConfig.deceasedPersonsEnabled) thenReturn false

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(Seq(
            bind(classOf[EstatesStoreConnector]).toInstance(mockEstatesStoreConnector),
            bind(classOf[EstatesConnector]).toInstance(mockEstatesConnector),
            bind[FrontendAppConfig].toInstance(mockAppConfig),
            bind[SessionRepository].toInstance(mockRepository)
          ))
          .build()

        val request = FakeRequest(GET, controllers.registration_progress.routes.TaskListController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(None, sections, isTaskListComplete = true)(fakeRequest, messages).toString

        application.stop()
      }

      "no tasks completed" in {

        val sections = List(
          Task(Link(EstateDetails, estateDetailsRoute), Some(InProgress)),
          Task(Link(PersonalRep, personalRepRoute), Some(InProgress)),
          Task(Link(DeceasedPersons, deceasedPersonsRoute), Some(InProgress))
        )

        when(mockRepository.set(any())).thenReturn(Future.successful(true))

        when(mockEstatesStoreConnector.getStatusOfTasks(any(), any()))
          .thenReturn(Future.successful(CompletedTasks(details = false, personalRepresentative = false, deceased = false)))

        when(mockEstatesConnector.getEstateName()(any(), any())).thenReturn(Future.successful(None))

        when(mockAppConfig.estateDetailsEnabled) thenReturn true
        when(mockAppConfig.personalRepEnabled) thenReturn true
        when(mockAppConfig.deceasedPersonsEnabled) thenReturn true

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(Seq(
            bind(classOf[EstatesStoreConnector]).toInstance(mockEstatesStoreConnector),
            bind(classOf[EstatesConnector]).toInstance(mockEstatesConnector),
            bind[FrontendAppConfig].toInstance(mockAppConfig),
            bind[SessionRepository].toInstance(mockRepository)
          ))
          .build()

        val request = FakeRequest(GET, controllers.registration_progress.routes.TaskListController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(None, sections, isTaskListComplete = false)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "return InternalServerError when error retrieving tasks statuses" in {


      when(mockEstatesStoreConnector.getStatusOfTasks(any(), any()))
        .thenReturn(Future.successful(InternalServerError))

      when(mockEstatesConnector.getEstateName()(any(), any())).thenReturn(Future.successful(None))

      when(mockRepository.set(any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(Seq(
          bind[SessionRepository].toInstance(mockRepository),
          bind(classOf[EstatesStoreConnector]).toInstance(mockEstatesStoreConnector),
          bind(classOf[EstatesConnector]).toInstance(mockEstatesConnector)
        ))
        .build()

      val request = FakeRequest(GET, controllers.registration_progress.routes.TaskListController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }
  }
}
