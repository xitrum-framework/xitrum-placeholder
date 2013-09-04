package placeholder

import org.jboss.netty.handler.codec.http.HttpHeaders
import akka.actor.{ActorSystem,Props}
import xitrum.{ActionActor,Server}
import xitrum.annotation.{GET, Error404, Error500}
import placeholder.model._
import placeholder.actor._

object Boot {
  def main(args: Array[String]) {
    //TODO: create threads pool
    //TODO: regist actors
    Server.start()
  }
}

@Error404
class NotFoundError extends ActionActor {
  def execute() {
    respondFile("public/400.html")
  }
}

@Error500
class ServerError extends ActionActor {
  def execute() {
    respondFile("public/500.html")
  }
}

@GET("/")
class SiteIndex extends ActionActor{
  def execute() =
    respondView()
}

@GET("/:width")
class SquareActor extends ActionActor {
  def execute() {
    val width     = param[Int]("width")
    val color     = paramo("color")
    val text      = paramo("text")
    val textcolor = paramo("textcolor")

    val color_val = color match {
      case Some(color) => color
      case None => "GRAY"
    }
    val text_val = text match {
      case Some(text) => text
      case None => "placeholder"
    }
    val textcolor_val = textcolor match {
      case Some(textcolor) => textcolor
      case None => "WHITE"
    }

    val shape = new Square(color_val,text_val,textcolor_val,width)

    //TODO : use registory
    val system = ActorSystem("system")
    val actor = system.actorOf(Props[CanvasActor], "square")

    logger.error("SquareActor:actor!shape")
    actor ! shape
  }

  override def receive = {
    case bytes: Array[Byte] =>
      render(bytes)
    case _ =>
      logger.error("SquareActor:Unexpected message")
  }

  def render(bytes: Array[Byte]) = {
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes);
  }
}

//@GET("/:width/:height")
//class RectangleActor extends ActionActor {
//  def execute() {
//    val width     = param[Int]("width")
//    val height    = param[Int]("height")
//    val color     = paramo("color")
//    val text      = paramo("text")
//    val textcolor = paramo("textcolor")
//
//    val bytes = Canvas.renderRectangle(width, height)
//    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
//    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
//    respondBinary(bytes);
//  }
//}
//
//@GET("/circle/:radius")
//class CircleActor extends ActionActor {
//  def execute() {
//    val radius = param[Int]("radius")
//    val color     = paramo("color")
//    val text      = paramo("text")
//    val textcolor = paramo("textcolor")
//
//    val bytes = Canvas.renderCircle(radius)
//    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
//    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
//    respondBinary(bytes);
//  }
//}
