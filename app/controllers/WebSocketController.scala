package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

import scala.concurrent.duration.FiniteDuration

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

@Singleton
class WebSocketController @Inject() (implicit system: ActorSystem, materializer: Materializer)  {

  import akka.actor._
  import play.api.libs.json._

//
//  implicit val inEventFormat = Json.format[InEvent]
//  implicit val outEventFormat = Json.format[OutEvent]

  import play.api.mvc.WebSocket.FrameFormatter

//  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[InEvent, OutEvent]

  object MyWebSocketActor {
    def props(out: ActorRef) = Props(new MyWebSocketActor(out))
  }

//  class MyWebSocketActor(out: ActorRef) extends Actor {
//    import play.api.libs.json.JsValue
//    def receive = {
//      case msg: JsValue =>
//        out ! msg
//    }
//  }

  object MessageA{
    implicit val format = Json.format[MessageA]
  }

  object MessageB {
    implicit val format = Json.format[MessageB]
  }

//  implicit val residentReads = Json.reads[MessageA]
//implicit val residentReads = (
//  (__ \ "data").read[String] and
//    (__ \ "messageType").read[String]
//  )(MessageA)

  abstract class ClientMessage()
  case class MessageA(data:String, messageType:String) extends ClientMessage
  case class MessageB(data:String, messageType:String = "b") extends ClientMessage

//  object ClientMessage {
//
//
    implicit def jsValue2ClientMessage(jsValue: JsValue): ClientMessage = {
      println((jsValue \ "messageType").as[String])
      (jsValue \ "messageType").as[String] match {
        case "a" => jsValue.as[MessageA]//MessageA( (jsValue \ "data").as[String])
        case "b" => MessageB( (jsValue \ "data").as[String])
      }
    }

    implicit def clientMessage2jsValue(clientMessage: ClientMessage): JsValue = {
      clientMessage match {
        case msgA: MessageA => Json.toJson(msgA)
        case msgB: MessageB => Json.toJson(msgB)
      }
    }

//  }

  class MyWebSocketActor(out: ActorRef) extends Actor {
    def receive = {
      case request: JsValue =>
        println("abc")
        val response = handleMessage(request)
        out ! response
    }

    def handleMessage(msg: ClientMessage): JsValue = {
      msg match {
        case msg: MessageA => Json.toJson(msg.data.toInt + 2)
        case msg: MessageB => Json.toJson(msg)
      }
    }

//    def receive = {
//      case msg: String =>{
//
//        println(self.path.toSerializationFormat)
//
////        val x = system.actorSelection(self.path.parent.toSerializationFormat)
////        x ! "Message12"
//
//        out ! ("I received your message: " + msg)
//      }
//    }
  }


//  def socket = WebSocket.accept[JsValue, JsValue] { request =>
//    val r =  ActorFlow.actorRef(out => MyWebSocketActor.props(out))
//    val self = system.actorSelection("/user/$a")
//    self ! "Message1"
//
//
//    r
//  }

  def socket = WebSocket.accept[JsValue, JsValue] { request =>
    println("Hello2")
    ActorFlow.actorRef(out => MyWebSocketActor.props(out))
  }


}
