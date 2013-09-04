package placeholder

import org.jboss.netty.handler.codec.http.HttpHeaders
import xitrum.ActionActor
import xitrum.Server
import xitrum.annotation.{GET, Error404, Error500}
import xitrum.validator._
import xitrum.exception.InvalidInput
import placeholder.model._

object Boot {
  def main(args: Array[String]) {
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

    val bytes = Canvas.renderSquare(width)
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
