package placeholder

import org.jboss.netty.handler.codec.http.HttpHeaders

import placeholder.model.Canvas
import xitrum.ActionActor
import xitrum.Server
import xitrum.annotation.GET

object Boot {
  def main(args: Array[String]) {
    Server.start()
  }
}

@GET("/")
class AdminIndex extends ActionActor {
  def execute() {
    respondInlineView(<p>Welcome</p>)
  }
}

@GET("/:width/:height")
class HelloActor extends ActionActor {
  def execute() {
    val width = param("width")
    val height = param("height")
    val bytes = Canvas.render(width.toInt, height.toInt)
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes);
  }
}

