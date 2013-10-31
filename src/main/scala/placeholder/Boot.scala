package placeholder

import scala.util.Properties
import scala.collection.JavaConversions._

import org.jboss.netty.handler.codec.http.HttpHeaders
import akka.actor.{Actor, ActorSystem, Props}

import xitrum.{Action, ActionActor, Server}
import xitrum.annotation.{First, GET, Swagger}

import placeholder.model._

//-----------------------------------------------------------------------------
// Future version

import scala.concurrent.{Future, ExecutionContext}
import xitrum.annotation.CacheActionDay

object Boot {
  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "8000")
    System.setProperty("xitrum.port.http", port)
    Server.start()
  }
}

@GET("")
@CacheActionDay(30)
class SiteIndex extends ActionActor {
  def execute() {
    respondView()
  }
}

//------------------------------------------------------------------------------

trait ShapeActor extends ActionActor {
  def send(shape: Shape) {
    val actorRef = Canvas.getActorRef
    actorRef ! shape
    context.become {
      case bytes: Array[Byte] =>
        render(bytes)

      case x =>
        log.error("SquareActor:Unexpected message: " + x)
    }
  }

  def render(bytes: Array[Byte]) {
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes)
  }
}

@Swagger(
  Swagger.OptStringQuery("color",     "Default: GRAY"),
  Swagger.OptStringQuery("text",      "Default: placeholder"),
  Swagger.OptStringQuery("textcolor", "Default: WHITE")
)
trait RenderOptions extends Action {
  lazy val color     = paramo("color").getOrElse("GRAY")
  lazy val text      = paramo("text").getOrElse("placeholder")
  lazy val textcolor = paramo("textcolor").getOrElse("WHITE")
}

@GET(":width")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate square image"),
  Swagger.IntPath("width")
)
class SquareActor extends ShapeActor with RenderOptions {
  def execute() {
    val width = param[Int]("width")
    val shape = new Square(color, text, textcolor, width)
    send(shape)
  }
}

@GET(":width/:height")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate rectangle image"),
  Swagger.IntPath("width"),
  Swagger.IntPath("height")
)
class RectangleActor extends ShapeActor with RenderOptions {
  def execute() {
    val width  = param[Int]("width")
    val height = param[Int]("height")
    val shape  = new Rectangle(color, text, textcolor, width, height)
    send(shape)
  }
}

@First
@GET("circle/:radius")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate circle image"),
  Swagger.IntPath("radius")
)
class CircleActor extends ShapeActor with RenderOptions {
  def execute() {
    val radius = param[Int]("radius")
    val shape  = new Circle(color, text, textcolor, radius)
    send(shape)
  }
}

trait ExContext {
  import java.util.concurrent.Executors
  import scala.collection.parallel
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallel.availableProcessors*2))
}

@First
@GET("future/:width")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate square image"),
  Swagger.IntPath("width")
)
class SquareFuture extends ActionActor with RenderOptions with ExContext {
  def execute() {
    val width = param[Int]("width")
    val shape = new Square(color, text, textcolor, width)

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
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate rectangle image"),
  Swagger.IntPath("width"),
  Swagger.IntPath("height")
)
class RectangleFuture extends ActionActor with RenderOptions with ExContext {
  override def execute() {
    val width  = param[Int]("width")
    val height = param[Int]("height")
    val shape  = new Rectangle(color, text, textcolor, width, height)

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

@GET("future/circle/:radius")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate circle image"),
  Swagger.IntPath("radius")
)
class CircleFuture extends ActionActor with RenderOptions with ExContext {
  override def execute() {
    val radius = param[Int]("radius")
    val shape  = new Circle(color, text, textcolor, radius)

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
