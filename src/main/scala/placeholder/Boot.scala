package placeholder

import org.jboss.netty.handler.codec.http.HttpHeaders
import xitrum.ActionActor
import xitrum.Server
import xitrum.annotation.GET
import xitrum.validator._
import xitrum.exception.InvalidInput
import placeholder.model._

object Boot {
  def main(args: Array[String]) {
    Server.start()
  }
}

@GET("/")
class SiteIndex extends ActionActor{
  def execute() =
    respondView()
}

@GET("/:width")
class WidthActor extends ActionActor {
  def execute() {
    val width = param[Int]("width")
    val bytes = Canvas.render(width, width)
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes);
  }
}

@GET("/:width/:height")
class HeightActor extends ActionActor {
  def execute() {
    val width = param[Int]("width")
    val height = param[Int]("height")
    val bytes = Canvas.render(width, height)
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes);
  }
}

@GET("/:width/:height/:option")
class OptionActor extends ActionActor {
  def execute() {
    val width = param[Int]("width")
    val height = param[Int]("height")
    val option = param("option")

    // TODO : handle option
    // option : color=white&text=hogehoge&textcolor=red&key=value


    val bytes = Canvas.render(width, height)
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes);
  }
}
