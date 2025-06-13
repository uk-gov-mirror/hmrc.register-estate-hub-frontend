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

package controllers

import java.time.LocalDateTime

import com.google.inject.Inject
import connectors.EstatesConnector
import controllers.actions.Actions
import forms.DeclarationFormProvider
import handlers.ErrorHandler
import models.Declaration
import models.http.{DeclarationResponse, TRNResponse}
import pages._
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import views.html.DeclarationView

import scala.concurrent.{ExecutionContext, Future}

class DeclarationController @Inject()(
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeclarationView,
                                       formProvider: DeclarationFormProvider,
                                       actions: Actions,
                                       repository: SessionRepository,
                                       connector: EstatesConnector,
                                       errorHandler: ErrorHandler
                                     )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  val form: Form[Declaration] = formProvider()

  def onPageLoad: Action[AnyContent] = actions.authWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(DeclarationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.affinityGroup))
  }

  def onSubmit: Action[AnyContent] = actions.authWithData.async {

    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, request.affinityGroup))),

        declaration => {

          connector.register(declaration) flatMap {
            case TRNResponse(trn) =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers
                  .set(DeclarationPage, declaration)
                  .flatMap(_.set(SubmissionDatePage, LocalDateTime.now))
                  .flatMap(_.set(TRNPage, trn))
                )
                _ <- repository.set(updatedAnswers)
              } yield {
                Redirect(controllers.routes.ConfirmationController.onPageLoad())
              }
            case DeclarationResponse.AlreadyRegistered =>
              logger.error(s"[Session ID: ${Session.id(hc)}] estate already registered")
              errorHandler.badRequestTemplate.map(html => BadRequest(html))
            case _ =>
              logger.error(s"[Session ID: ${Session.id(hc)}] something went wrong")
              errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
          }
        }
      )
  }
}
