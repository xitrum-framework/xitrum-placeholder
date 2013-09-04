package placeholder.actor

import java.awt.{Font,RenderingHints}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import xitrum.ActionActor
import placeholder.model.{Square,Rectangle,Circle}

/*
 * CanvasActor
 */
class CanvasActor extends ActionActor {
  def execute() {}

  override def receive = {
    case square: Square =>
      val bytes = renderSquare(square)
      sender ! bytes
    case rectangle: Rectangle =>
      logger.info("rectangle")
      // val bytes = renderRectangle(square)
      // sender ! bytes
    case circle: Circle =>
      logger.info("circle")
      // val bytes = renderCircle(square)
      // sender ! bytes
    case _ =>
      logger.error("CanvasActor:Unexpected message")
  }

  /**
   * renderSquare
   * @see http://otfried-cheong.appspot.com/scala/drawing.html
   * @see http://stackoverflow.com/questions/2658554/using-graphics2d-to-overlay-text-on-a-bufferedimage-and-return-a-bufferedimage
   */
  def renderSquare(square: Square): Array[Byte] = {
    val canvas = new BufferedImage(square.getWidth, square.getWidth, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(square.getColor)
    g.fillRect(0, 0, canvas.getWidth, canvas.getWidth)
    g.setFont(new Font("Serif", Font.BOLD, 20));
    var s = square.getText
    s = if ("placeholder" == s) canvas.getWidth.toString + "×" + canvas.getWidth.toString else s
    val fm = g.getFontMetrics
    val x = square.getWidth - fm.stringWidth(s) - 5;
    val y = fm.getHeight();
    g.drawString(s, x, y);
    g.dispose()
    val baos = new ByteArrayOutputStream();
    val raster = canvas.getRaster;
    ImageIO.write(canvas, "png", baos);
    baos.flush();
    val bytes = baos.toByteArray;
    baos.close();
    bytes
  }
}
