package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db._
import dao.CatDAO
import models.Cat
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(catDao: CatDAO) extends Controller {

  def index = Action.async {
    catDao.all().map {
      cats => Ok(views.html.index(cats))
    }
  }

  def insertCat = Action.async { implicit request =>
    val cat: Cat = catForm.bindFromRequest.get
    catDao.insert(cat).map(_ => Redirect(routes.HomeController.index))
  }

  val catForm = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Cat.apply)(Cat.unapply)
  )
}
