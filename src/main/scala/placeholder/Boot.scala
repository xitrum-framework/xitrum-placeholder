package placeholder

import scala.util.Properties

import io.netty.handler.codec.http.HttpHeaderNames

import xitrum.{Action, ActorAction, FutureAction, Server}
import xitrum.annotation.{First, GET, Swagger}

import placeholder.model._

//-----------------------------------------------------------------------------
// Future version

import scala.concurrent.Future
import xitrum.annotation.CacheActionDay

object Boot {
  def main(args: Array[String]): Unit = {
    val port = Properties.envOrElse("PORT", "8000")
    System.setProperty("xitrum.port.http", port)
    Server.start()
    Server.stopAtShutdown()
  }
}

@GET("")
@CacheActionDay(30)
class SiteIndex extends ActorAction {
  def execute(): Unit = {
    respondView()
  }
}

//------------------------------------------------------------------------------

trait ShapeActor extends ActorAction {
  def send(shape: Shape): Unit = {
    val actorRef = Canvas.getActorRef
    actorRef ! shape
    context.become {
      case bytes: Array[Byte] =>
        render(bytes)

      case x =>
        log.error("SquareActor:Unexpected message: " + x)
    }
  }

  def render(bytes: Array[Byte]): Unit = {
    response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
    response.headers.set(HttpHeaderNames.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes)
  }
}

@Swagger(
  Swagger.Produces("image/png"),
  Swagger.OptStringQuery("color",     "Default: GRAY"),
  Swagger.OptStringQuery("text",      "Default: placeholder"),
  Swagger.OptStringQuery("textcolor", "Default: WHITE")
)
trait RenderOptions extends Action {
  lazy val color:     String = paramo("color").getOrElse("GRAY")
  lazy val text:      String = paramo("text").getOrElse("placeholder")
  lazy val textcolor: String = paramo("textcolor").getOrElse("WHITE")
}

@GET(":width<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate square image with Actor"),
  Swagger.IntPath("width")
)
class SquareActor extends ShapeActor with RenderOptions {
  def execute(): Unit = {
    val width = param[Int]("width")
    val shape = new Square(color, text, textcolor, width)
    send(shape)
  }
}

@GET(":width<\\d+>/:height<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate rectangle image with Actor"),
  Swagger.IntPath("width"),
  Swagger.IntPath("height")
)
class RectangleActor extends ShapeActor with RenderOptions {
  def execute(): Unit = {
    val width  = param[Int]("width")
    val height = param[Int]("height")
    val shape  = new Rectangle(color, text, textcolor, width, height)
    send(shape)
  }
}

@First
@GET("circle/:radius<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate circle image with Actor"),
  Swagger.IntPath("radius")
)
class CircleActor extends ShapeActor with RenderOptions {
  def execute(): Unit = {
    val radius = param[Int]("radius")
    val shape  = new Circle(color, text, textcolor, radius)
    send(shape)
  }
}

//trait ExContext {
//  import java.util.concurrent.Executors
//  import scala.collection.parallel
//  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallel.availableProcessors*2))
//}

@First
@GET("future/:width<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate square image with Future"),
  Swagger.IntPath("width")
)
class SquareFuture extends Action with RenderOptions{
  def execute(): Unit = {
    val width = param[Int]("width")
    val shape = new Square(color, text, textcolor, width)

    val render = Future { Renderer.renderSquare(shape) }
    render.foreach { result: Array[Byte] =>
        response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
        response.headers.set(HttpHeaderNames.CONTENT_LENGTH, result.length)
        respondBinary(result)
    }
  }
}

@GET("future/:width<\\d+>/:height<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate rectangle image with Future"),
  Swagger.IntPath("width"),
  Swagger.IntPath("height")
)
class RectangleFuture extends Action with RenderOptions {
  override def execute(): Unit = {
    val width  = param[Int]("width")
    val height = param[Int]("height")
    val shape  = new Rectangle(color, text, textcolor, width, height)

    val render = Future { Renderer.renderRectangle(shape) }
    render.foreach { result: Array[Byte] =>
        response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
        response.headers.set(HttpHeaderNames.CONTENT_LENGTH, result.length)
        respondBinary(result)
    }
  }
}

@GET("future/circle/:radius<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate circle image with Future"),
  Swagger.IntPath("radius")
)
class CircleFuture extends Action with RenderOptions {
  override def execute(): Unit = {
    val radius = param[Int]("radius")
    val shape  = new Circle(color, text, textcolor, radius)

    val render = Future { Renderer.renderCircle(shape) }
    render.foreach { result: Array[Byte] =>
        response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
        response.headers.set(HttpHeaderNames.CONTENT_LENGTH, result.length)
        respondBinary(result)
    }
  }
}

@First
@GET("futureAction/:width<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate square image with xitrum's FutreAction"),
  Swagger.IntPath("width")
)
class SquareFutureAction extends FutureAction with RenderOptions{
  def execute(): Unit = {
    val width = param[Int]("width")
    val shape = new Square(color, text, textcolor, width)
    val bytes = Renderer.renderSquare(shape)
    response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
    response.headers.set(HttpHeaderNames.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes)
  }
}

@GET("futureAction/:width<\\d+>/:height<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate rectangle image with xitrum's FutreAction"),
  Swagger.IntPath("width"),
  Swagger.IntPath("height")
)
class RectangleFutureActor extends FutureAction with RenderOptions {
  def execute(): Unit = {
    val width  = param[Int]("width")
    val height = param[Int]("height")
    val shape  = new Rectangle(color, text, textcolor, width, height)
    val bytes  = Renderer.renderRectangle(shape)
    response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
    response.headers.set(HttpHeaderNames.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes)
  }
}

@First
@GET("futureAction/circle/:radius<\\d+>")
@CacheActionDay(30)
@Swagger(
  Swagger.Summary("Generate circle image with xitrum's FutreAction"),
  Swagger.IntPath("radius")
)
class CircleFutureActor extends FutureAction with RenderOptions {
  def execute(): Unit = {
    val radius = param[Int]("radius")
    val shape  = new Circle(color, text, textcolor, radius)
    val bytes  = Renderer.renderCircle(shape)
    response.headers.set(HttpHeaderNames.CONTENT_TYPE,  "image/png")
    response.headers.set(HttpHeaderNames.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes)
  }
}
