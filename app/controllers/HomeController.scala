package controllers

import javax.inject._

import akka.actor.{ActorSystem, _}
import dao.CatDAO
import models._
import play.api.cache.CacheApi
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */

@Singleton
class HomeController @Inject()(implicit system: ActorSystem, catDao: CatDAO, cache: CacheApi) extends Controller {

  def index = Action.async {
    val actorId = cache.get[String]("actor-id_1").get
    println(actorId)
    val client = system.actorSelection(actorId)
    client ! Json.toJson(EventB("MessageB"))

    catDao.all().map {
      cats => Ok(views.html.index(cats))
    }
  }

  def insertCat = Action.async { implicit request =>
    val cat: Cat = catForm.bindFromRequest.get
    catDao.insert(cat).map(_ => Redirect(routes.HomeController.index))
  }

  def ws = Action { request => Ok(views.html.ws()) }

  val catForm = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Cat.apply)(Cat.unapply)
  )
}
