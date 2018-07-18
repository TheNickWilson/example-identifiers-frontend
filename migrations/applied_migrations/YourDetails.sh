#!/bin/bash

echo "Applying migration YourDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourDetails                       controllers.YourDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourDetails                       controllers.YourDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourDetails                       controllers.YourDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourDetails                       controllers.YourDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourDetails.title = yourDetails" >> ../conf/messages.en
echo "yourDetails.heading = yourDetails" >> ../conf/messages.en
echo "yourDetails.field1 = Field 1" >> ../conf/messages.en
echo "yourDetails.field2 = Field 2" >> ../conf/messages.en
echo "yourDetails.checkYourAnswersLabel = yourDetails" >> ../conf/messages.en
echo "yourDetails.error.field1.required = Enter field1" >> ../conf/messages.en
echo "yourDetails.error.field2.required = Enter field2" >> ../conf/messages.en
echo "yourDetails.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "yourDetails.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourDetails: Option[YourDetails] = cacheMap.getEntry[YourDetails](YourDetailsId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourDetails: Option[AnswerRow] = userAnswers.yourDetails map {";\
     print "    x => AnswerRow(\"yourDetails.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.YourDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration YourDetails completed"
