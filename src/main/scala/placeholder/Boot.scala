package placeholder

import util.Properties

import org.jboss.netty.handler.codec.http.HttpHeaders
import akka.actor.{Actor, ActorSystem, Props}

import scala.collection.JavaConversions._

import xitrum.{Action, ActionActor, Server}
import xitrum.annotation.{First, GET, Error404, Error500}

import placeholder.model._

object Boot {
  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "8000")
    System.setProperty("xitrum.port.http", port)
    Server.start()
  }
}

class ShapeActor extends ActionActor {
  def execute(){}
  def send(shape: Shape){
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
  def execute(){
    logger.debug("request: " + request)
    respondView()
  }
}

@GET("/:width")
class SquareActor extends ShapeActor {
  override def execute() {
    logger.debug("request: " + request)
    /*request.getHeaderNames().toList.foreach { key =>
      logger.warn("request=> %s : %s".format(key,request.getHeader(key)))
    }
    textParams.keySet.foreach { key =>
      logger.warn("textparams=> %s : %s".format(key,textParams.get(key)))
    }*/
    val width     = param[Int]("width")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape = new Square(color, text, textcolor, width)
    send(shape)
  }
}

@GET("/:width/:height")
class RectangleActor extends ShapeActor {
  override def execute() {
    val width     = param[Int]("width")
    val height    = param[Int]("height")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape = new Rectangle(color, text, textcolor, width, height)
    send(shape)
  }
}

@GET("/circle/:radius")
class CircleActor extends ShapeActor {
  override def execute() {
    val radius = param[Int]("radius")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape = new Circle(color, text, textcolor, radius)
    send(shape)
  }
}

// ######################################################
// Future version
// ######################################################
import scala.concurrent.{Future, ExecutionContext}
trait ExContext {
  import java.util.concurrent.Executors
  import scala.collection.parallel
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallel.availableProcessors*2))
}

@First
@GET("/future/:width")
class ShapeFuture extends ActionActor with ExContext {
  def execute() {
    val width     = param[Int]("width")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape = new Square(color, text, textcolor, width)

    val render = Future { Renderer.renderSquare(shape) }
    render.onSuccess {
      case result: Array[Byte] =>
        self ! result
    }

    context.become {
      case result: Array[Byte] =>
        respondBinary(result)
    }
  }
}
@GET("/future/:width/:height")
class RectangleFuture extends ActionActor with ExContext {
  override def execute() {
    val width     = param[Int]("width")
    val height    = param[Int]("height")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape = new Rectangle(color, text, textcolor, width, height)

    val render = Future { Renderer.renderRectangle(shape) }
    render.onSuccess {
      case result: Array[Byte] =>
        self ! result
    }

    context.become {
      case result: Array[Byte] =>
        respondBinary(result)
    }
  }
}

@GET("/future/circle/:radius")
class CircleFuture extends ActionActor with ExContext {
  override def execute() {
    val radius = param[Int]("radius")
    val color     = paramo("color").getOrElse("GRAY")
    val text      = paramo("text").getOrElse("placeholder")
    val textcolor = paramo("textcolor").getOrElse("WHITE")
    val shape = new Circle(color, text, textcolor, radius)

    val render = Future { Renderer.renderCircle(shape) }
    render.onSuccess {
      case result: Array[Byte] =>
        self ! result
    }

    context.become {
      case result: Array[Byte] =>
        respondBinary(result)
    }
  }
}