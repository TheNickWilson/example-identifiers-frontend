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

package models

import play.api.libs.json._
import utils.{Enumerable, RadioOption, WithName}

sealed trait Location

object Location {

  case object England extends WithName("england") with Location
  case object Scotland extends WithName("scotland") with Location
  case object Wales extends WithName("wales") with Location
  case object NorthernIreland extends WithName("northernIreland") with Location

  val values: Set[Location] = Set(
    England, Scotland, Wales, NorthernIreland
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("location", value.toString)
  }

  implicit val enumerable: Enumerable[Location] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object LocationWrites extends Writes[Location] {
    def writes(location: Location) = Json.toJson(location.toString)
  }

  implicit object LocationReads extends Reads[Location] {
    override def reads(json: JsValue): JsResult[Location] = json match {
      case JsString(England.toString)         => JsSuccess(England)
      case JsString(Wales.toString)           => JsSuccess(Wales)
      case JsString(Scotland.toString)        => JsSuccess(Scotland)
      case JsString(NorthernIreland.toString) => JsSuccess(NorthernIreland)
      case _                                  => JsError("Unknown location")
    }
  }
}
