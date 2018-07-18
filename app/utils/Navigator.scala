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

package utils

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import identifiers._
import models.{CheckMode, Location, Mode, NormalMode}

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId             -> locationRoute,
    ChildAgedTwoId         -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.YourDetailsController.onPageLoad(NormalMode)),
    YourDetailsId          -> (_ => routes.CheckYourAnswersController.onPageLoad())
  )

  private def locationRoute(answers: UserAnswers) = answers.location match {
    case Some(Location.NorthernIreland) => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
    case Some(_)                        => routes.ChildAgedTwoController.onPageLoad(NormalMode)
    case None                           => routes.SessionExpiredController.onPageLoad()
  }

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> locationCheckRoute
  )

  private def locationCheckRoute(answers: UserAnswers) =
    (answers.location, answers.childAgedTwo) match {
      case (Some(Location.NorthernIreland), _)    => routes.CheckYourAnswersController.onPageLoad()
      case (Some(_)                       , None) => routes.ChildAgedTwoController.onPageLoad(CheckMode)
      case _                                      => routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad())
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
