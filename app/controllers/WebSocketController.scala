package controllers

import javax.inject._

import akka.actor.{ActorSystem, _}
import akka.stream.Materializer
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import models._
import play.api.cache.{Cache, CacheApi}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

@Singleton
class WebSocketController @Inject()(implicit system: ActorSystem, materializer: Materializer, cache: CacheApi) {

  object MyWebSocketActor {
    def props(out: ActorRef) = Props(new MyWebSocketActor(out))
  }

  class MyWebSocketActor(out: ActorRef) extends Actor {
    override def preStart() {
      println("open")
      println(self.path.parent.toSerializationFormat)
      val x = system.actorSelection(self.path.parent.toSerializationFormat)
      x ! "Message12"
    }

    override def postStop() {
      println("close")
    }

    override def receive = {
      case request: JsValue =>
        val response = handleMessage(request)
        out ! response
    }

    def handleMessage(event: Event): JsValue = {
      event match {
        case event: EventA => {
          val actorId = self.path.parent.toSerializationFormat
          cache.set("actor-id_" + event.data, actorId)
          Json.toJson(event)
        }
        case event: EventB => Json.toJson(event)
      }
    }
  }

  def socket = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(out))

  }

}