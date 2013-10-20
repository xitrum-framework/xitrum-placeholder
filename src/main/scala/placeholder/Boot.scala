package placeholder

import scala.util.Properties

import org.jboss.netty.handler.codec.http.HttpHeaders
import akka.actor.{Actor, ActorSystem, Props}

import scala.collection.JavaConversions._

import xitrum.{Action, ActionActor, Server}
import xitrum.annotation.{First, GET, Swagger}

import placeholder.model._

object Boot {
  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "8000")
    System.setProperty("xitrum.port.http", port)
    Server.start()
  }
}

trait ShapeActor extends ActionActor {
  def send(shape: Shape) {
    val actorRef = Canvas.getActorRef
    actorRef ! shape
    context.become {
      case bytes: Array[Byte] =>
        render(bytes)

      case x =>
        logger.error("SquareActor:Unexpected message: " + x)
    }
  }

  def render(bytes: Array[Byte]) {
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes)
  }
}

@GET("")
class SiteIndex extends ActionActor {
  def execute() {
    respondView()
  }
}

@GET(":width")
@Swagger(
  "Generate square image",
  Swagger.IntPath("width"),
  Swagger.OptionalStringQuery("color",     "Default: GRAY"),
  Swagger.OptionalStringQuery("text",      "Default: placeholder"),
  Swagger.OptionalStringQuery("textcolor", "Default: WHITE")
)
class SquareActor extends ShapeActor {
  def execute() {
    val width     = param[Int]("width")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape     = new Square(color, text, textcolor, width)
    send(shape)
  }
}

@GET(":width/:height")
@Swagger(
  "Generate rectangle image",
  Swagger.IntPath("width"),
  Swagger.IntPath("height"),
  Swagger.OptionalStringQuery("color",     "Default: GRAY"),
  Swagger.OptionalStringQuery("text",      "Default: placeholder"),
  Swagger.OptionalStringQuery("textcolor", "Default: WHITE")
)
class RectangleActor extends ShapeActor {
  def execute() {
    val width     = param[Int]("width")
    val height    = param[Int]("height")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape     = new Rectangle(color, text, textcolor, width, height)
    send(shape)
  }
}

@First
@GET("circle/:radius")
@Swagger(
  "Generate circle image",
  Swagger.IntPath("radius"),
  Swagger.OptionalStringQuery("color",     "Default: GRAY"),
  Swagger.OptionalStringQuery("text",      "Default: placeholder"),
  Swagger.OptionalStringQuery("textcolor", "Default: WHITE")
)
class CircleActor extends ShapeActor {
  def execute() {
    val radius    = param[Int]("radius")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape     = new Circle(color, text, textcolor, radius)
    send(shape)
  }
}

//-----------------------------------------------------------------------------
// Future version

import scala.concurrent.{Future, ExecutionContext}

trait ExContext {
  import java.util.concurrent.Executors
  import scala.collection.parallel
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallel.availableProcessors*2))
}

@First
@GET("future/:width")
class ShapeFuture extends ActionActor with ExContext {
  def execute() {
    val width     = param[Int]("width")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape     = new Square(color, text, textcolor, width)

    val render = Future { Renderer.renderSquare(shape) }
    render.onSuccess {
      case result: Array[Byte] =>
        self ! result
    }

    context.become {
      case result: Array[Byte] =>
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, result.length)
        respondBinary(result)
    }
  }
}
@GET("future/:width/:height")
class RectangleFuture extends ActionActor with ExContext {
  override def execute() {
    val width     = param[Int]("width")
    val height    = param[Int]("height")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape     = new Rectangle(color, text, textcolor, width, height)

    val render = Future { Renderer.renderRectangle(shape) }
    render.onSuccess {
      case result: Array[Byte] =>
        self ! result
    }

    context.become {
      case result: Array[Byte] =>
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, result.length)
        respondBinary(result)
    }
  }
}

@First
@GET("future/circle/:radius")
class CircleFuture extends ActionActor with ExContext {
  override def execute() {
    val radius    = param[Int]("radius")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape     = new Circle(color, text, textcolor, radius)

    val render = Future { Renderer.renderCircle(shape) }
    render.onSuccess {
      case result: Array[Byte] =>
        self ! result
    }

    context.become {
      case result: Array[Byte] =>
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, result.length)
        respondBinary(result)
    }
  }
}
