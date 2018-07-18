#!/bin/bash

echo "Applying migration Location"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /location               controllers.LocationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /location               controllers.LocationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLocation               controllers.LocationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLocation               controllers.LocationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "location.title = Where do you live?" >> ../conf/messages.en
echo "location.heading = Where do you live?" >> ../conf/messages.en
echo "location.england = England" >> ../conf/messages.en
echo "location.scotland = Scotland" >> ../conf/messages.en
echo "location.checkYourAnswersLabel = Where do you live?" >> ../conf/messages.en
echo "location.error.required = Please give an answer for location" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def location: Option[Location] = cacheMap.getEntry[Location](LocationId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def location: Option[AnswerRow] = userAnswers.location map {";\
     print "    x => AnswerRow(\"location.checkYourAnswersLabel\", s\"location.$x\", true, routes.LocationController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration Location completed"
