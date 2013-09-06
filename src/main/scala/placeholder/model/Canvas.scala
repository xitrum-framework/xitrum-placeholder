package placeholder.model

import java.awt.{AlphaComposite, Color, Font, RenderingHints, Transparency}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import akka.actor.{Actor, ActorRef, Props}
import xitrum.{Config, Logger}

object Renderer {
  def renderSquare(square: Square): Array[Byte] = {
    // See http://otfried-cheong.appspot.com/scala/drawing.html
    val canvas = new BufferedImage(square.getWidth, square.getWidth, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(square.getColor)
    g.fillRect(0, 0, square.getWidth, square.getWidth)

    var fontSize = if (square.getWidth < 100) 10 else 20
    g.setFont(new Font("Verdana", Font.BOLD, fontSize))
    var s = square.getText
    s = if ("placeholder" == s) square.getWidth.toString + "x" + square.getWidth.toString else s
    val fm = g.getFontMetrics
    val x = square.getWidth/2 - fm.stringWidth(s)/2
    val y = square.getWidth/2 + fm.getHeight()/2
    g.setColor(square.getTextColor)
    g.drawString(s, x, y)

    g.dispose()
    val baos = new ByteArrayOutputStream()
    val raster = canvas.getRaster
    ImageIO.write(canvas, "png", baos)
    baos.flush()
    val bytes = baos.toByteArray
    baos.close()
    bytes
  }

  def renderRectangle(rectangle: Rectangle): Array[Byte] = {
    val canvas = new BufferedImage(rectangle.getWidth, rectangle.getHeight, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(rectangle.getColor)
    g.fillRect(0, 0, rectangle.getWidth, rectangle.getHeight)

    var fontSize = if (rectangle.getHeight < 100) 10 else 20
    g.setFont(new Font("Verdana", Font.BOLD, fontSize))
    var s = rectangle.getText
    s = if ("placeholder" == s) rectangle.getWidth.toString + "x" + rectangle.getHeight.toString else s
    val fm = g.getFontMetrics
    val x = rectangle.getWidth/2 - fm.stringWidth(s)/2
    val y = rectangle.getHeight/2 + fm.getHeight()/2
    g.setColor(rectangle.getTextColor)
    g.drawString(s, x, y)

    g.dispose()
    val baos = new ByteArrayOutputStream()
    val raster = canvas.getRaster
    ImageIO.write(canvas, "png", baos)
    baos.flush()
    val bytes = baos.toByteArray
    baos.close()
    bytes
  }

  def renderCircle(circle: Circle): Array[Byte] = {
  // https://weblogs.java.net/blog/campbell/archive/2006/07/java_2d_tricker.html
    val canvas = new BufferedImage(circle.getRadius*2, circle.getRadius*2, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()

    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    val gc = g.getDeviceConfiguration();
    val img = gc.createCompatibleImage(circle.getRadius*2, circle.getRadius*2, Transparency.TRANSLUCENT);
    val g2 = img.createGraphics();
    g2.setComposite(AlphaComposite.Clear)
    g2.fillRect(0, 0, circle.getRadius*2, circle.getRadius*2)
    g2.setComposite(AlphaComposite.Src)
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setColor(circle.getColor)
    g2.fillOval(0, 0, circle.getRadius*2, circle.getRadius*2)

    var fontSize = if (circle.getRadius*2 < 30) 10 else 20
    g2.setFont(new Font("Verdana", Font.BOLD, fontSize))
    var s = circle.getText
    s = if ("placeholder" == s) circle.getRadius.toString else s
    val fm = g2.getFontMetrics
    val x = circle.getRadius - fm.stringWidth(s)/2
    val y = circle.getRadius + fm.getHeight()/2
    g2.setColor(circle.getTextColor)
    g2.drawString(s, x, y);

    g.drawImage(img, 0, 0, null);

    g.dispose()
    val baos = new ByteArrayOutputStream()
    val raster = canvas.getRaster
    ImageIO.write(canvas, "png", baos)
    baos.flush()
    val bytes = baos.toByteArray
    baos.close()
    bytes
  }
}

object Canvas {

  // TODO use config file
  val actorRef1 = Config.actorSystem.actorOf(Props[CanvasActor], "canvas1")
  val actorRef2 = Config.actorSystem.actorOf(Props[CanvasActor], "canvas2")
  val actorRef3 = Config.actorSystem.actorOf(Props[CanvasActor], "canvas3")
  val actorRef4 = Config.actorSystem.actorOf(Props[CanvasActor], "canvas4")

  // RoundRobin
  // http://doc.akka.io/docs/akka/2.2.1/scala/routing.html
  var index = 1;
  def getActorRef : ActorRef = {
    index match {
      case 1 => index = 2;return actorRef1
      case 2 => index = 3;return actorRef2
      case 3 => index = 4;return actorRef3
      case 4 => index = 1;return actorRef4
    }
  }
}

class CanvasActor extends Actor with Logger {
  def receive = {
    case rectangle: Rectangle =>
      val bytes = Renderer.renderRectangle(rectangle)
      sender ! bytes

    case square: Square =>
      val bytes = Renderer.renderSquare(square)
      sender ! bytes

    case circle: Circle =>
      val bytes = Renderer.renderCircle(circle)
      sender ! bytes

    case _ =>
      logger.error("CanvasActor:Unexpected message")
  }
}
