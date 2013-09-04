package placeholder.actor

import java.awt.{Font,RenderingHints}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import org.jboss.netty.handler.codec.http.HttpHeaders
import javax.imageio.ImageIO
import xitrum.ActionActor
import xitrum.Logger
import placeholder.model.Square

/*
 * CanvasActor
 */

class CanvasActor extends ActionActor with Logger {
  def execute() {}

  def render(canvas: BufferedImage) = {
    val baos = new ByteArrayOutputStream();
    val raster = canvas.getRaster;
    ImageIO.write(canvas, "png", baos);
    baos.flush();
    val bytes = baos.toByteArray;
    baos.close();
    response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png")
    response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    respondBinary(bytes);
  }

  override def receive = {
    case square: Square =>
    //      logger.info("received test")

    //    case rectangle: Rectangle =>
    //      logger.info("received test")

    //    case circle: Circle =>
    //      logger.info("received test")

    case _ =>
      logger.error("unexpected message")
  }
  def renderSquare(square: Square) = {
    val canvas = new BufferedImage(square.getWidth, square.getWidth, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(square.getColor)
    g.fillRect(0, 0, canvas.getWidth, canvas.getWidth)
    g.setFont(new Font("Serif", Font.BOLD, 20));
    val s = square.getText
    val fm = g.getFontMetrics
    val x = square.getWidth - fm.stringWidth(s) - 5;
    val y = fm.getHeight();
    g.drawString(s, x, y);
    g.dispose()
    render(canvas);
  }
}
