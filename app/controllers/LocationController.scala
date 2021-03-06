/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.LocationFormProvider
import identifiers.LocationId
import models.Mode
import models.Location
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.location

import scala.concurrent.Future

class LocationController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        formProvider: LocationFormProvider) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (identify andThen getData) {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(_.location) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(location(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(location(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[Location](request.internalId, LocationId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(LocationId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
