#!/bin/bash

echo "Applying migration ChildAgedThreeOrFour"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childAgedThreeOrFour                       controllers.ChildAgedThreeOrFourController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childAgedThreeOrFour                       controllers.ChildAgedThreeOrFourController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildAgedThreeOrFour                       controllers.ChildAgedThreeOrFourController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildAgedThreeOrFour                       controllers.ChildAgedThreeOrFourController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childAgedThreeOrFour.title = childAgedThreeOrFour" >> ../conf/messages.en
echo "childAgedThreeOrFour.heading = childAgedThreeOrFour" >> ../conf/messages.en
echo "childAgedThreeOrFour.checkYourAnswersLabel = childAgedThreeOrFour" >> ../conf/messages.en
echo "childAgedThreeOrFour.error.required = Please give an answer for childAgedThreeOrFour" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedThreeOrFourId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childAgedThreeOrFour: Option[AnswerRow] = userAnswers.childAgedThreeOrFour map {";\
     print "    x => AnswerRow(\"childAgedThreeOrFour.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ChildAgedThreeOrFour completed"
