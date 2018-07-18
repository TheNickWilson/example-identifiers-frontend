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

import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import controllers.routes
import identifiers._
import models._
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import uk.gov.hmrc.http.cache.client.CacheMap

class NavigatorSpec extends SpecBase with MockitoSugar with PropertyChecks {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go from Location to ChildAgedTwo" when {

        "the user's Location is not Northern Ireland" in {

          val gen = Gen.oneOf(Location.England, Location.Wales, Location.Scotland)

          forAll(gen) {
            location =>
              val mockAnswers = mock[UserAnswers]
              when(mockAnswers.location) thenReturn Some(location)

              val result = navigator.nextPage(LocationId, NormalMode)(mockAnswers)
              result mustEqual routes.ChildAgedTwoController.onPageLoad(NormalMode)
          }
        }
      }

      "go from Location to ChildAgedThreeOrFour" when {

        "the user's Location is Northern Ireland" in {

          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.location) thenReturn Some(Location.NorthernIreland)

          val result = navigator.nextPage(LocationId, NormalMode)(mockAnswers)
          result mustEqual routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
        }
      }

      "go from ChildAgedTwo to ChildAgedThreeOrFour" in {
        val result = navigator.nextPage(ChildAgedTwoId, NormalMode)(mock[UserAnswers])
        result mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go from ChildAgedThreeOrFour to YourDetails" in {
        val result = navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(mock[UserAnswers])
        result mustBe routes.YourDetailsController.onPageLoad(NormalMode)
      }

      "go from YourDetails to CheckYourAnswers" in {
        val result = navigator.nextPage(YourDetailsId, NormalMode)(mock[UserAnswers])
        result mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "go from Location to CheckYourAnswers" when {

        "the user's Location is Northern Ireland" in {

          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.location) thenReturn Some(Location.NorthernIreland)
          when(mockAnswers.childAgedTwo) thenReturn None

          val result = navigator.nextPage(LocationId, CheckMode)(mockAnswers)
          result mustEqual routes.CheckYourAnswersController.onPageLoad()
        }

        "the user's Location not is Northern Ireland and they have already answered ChildAgedTwo" in {

          val gen = Gen.oneOf(Location.England, Location.Wales, Location.Scotland)

          forAll(gen, arbitrary[Boolean]) {
            case (location, childAgedTwo) =>

              val mockAnswers = mock[UserAnswers]
              when(mockAnswers.location) thenReturn Some(location)
              when(mockAnswers.childAgedTwo) thenReturn Some(childAgedTwo)

              val result = navigator.nextPage(LocationId, CheckMode)(mockAnswers)
              result mustEqual routes.CheckYourAnswersController.onPageLoad()
          }
        }
      }

      "go from Location to ChildAgedTwo" when {

        "the user's Location is not Northern Ireland and they have not answered ChildAgedTwo" in {

          val gen = Gen.oneOf(Location.England, Location.Wales, Location.Scotland)

          forAll(gen) {
            location =>

              val mockAnswers = mock[UserAnswers]
              when(mockAnswers.location) thenReturn Some(location)
              when(mockAnswers.childAgedTwo) thenReturn None

              val result = navigator.nextPage(LocationId, CheckMode)(mockAnswers)
              result mustEqual routes.ChildAgedTwoController.onPageLoad(CheckMode)
          }
        }
      }

      "go from ChildAgedTwo to CheckYourAnswers" in {
        val result = navigator.nextPage(ChildAgedTwoId, CheckMode)(mock[UserAnswers])
        result mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "go from ChildAgedThreeOrFour to CheckYourAnswers" in {
        val result = navigator.nextPage(ChildAgedThreeOrFourId, CheckMode)(mock[UserAnswers])
        result mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "go from YourDetails to CheckYourAnswers" in {
        val result = navigator.nextPage(YourDetailsId, CheckMode)(mock[UserAnswers])
        result mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
