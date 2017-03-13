package models

import play.api.libs.json.{JsValue, Json}

/**
  * Created by koduki on 2017/03/12.
  */

abstract class Event()

case class EventA(data: String, eventType: String = "a") extends Event

case class EventB(data: String, eventType: String = "b") extends Event

object EventA {
  implicit val format = Json.format[EventA]
}

object EventB {
  implicit val format = Json.format[EventB]
}

object Event {
  implicit def json2object(value: JsValue): Event = {
    (value \ "eventType").as[String] match {
      case "a" => value.as[EventA]
      case "b" => value.as[EventB]
    }
  }

  implicit def object2json(event: Event): JsValue = {
    event match {
      case event: EventA => Json.toJson(event)
      case event: EventB => Json.toJson(event)
    }
  }
}

