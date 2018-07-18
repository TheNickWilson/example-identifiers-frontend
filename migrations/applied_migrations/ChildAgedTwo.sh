#!/bin/bash

echo "Applying migration ChildAgedTwo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /childAgedTwo                       controllers.ChildAgedTwoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /childAgedTwo                       controllers.ChildAgedTwoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChildAgedTwo                       controllers.ChildAgedTwoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChildAgedTwo                       controllers.ChildAgedTwoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "childAgedTwo.title = childAgedTwo" >> ../conf/messages.en
echo "childAgedTwo.heading = childAgedTwo" >> ../conf/messages.en
echo "childAgedTwo.checkYourAnswersLabel = childAgedTwo" >> ../conf/messages.en
echo "childAgedTwo.error.required = Please give an answer for childAgedTwo" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def childAgedTwo: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedTwoId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def childAgedTwo: Option[AnswerRow] = userAnswers.childAgedTwo map {";\
     print "    x => AnswerRow(\"childAgedTwo.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ChildAgedTwoController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ChildAgedTwo completed"
