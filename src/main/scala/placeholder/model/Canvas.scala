package placeholder.model

import java.awt.{Color,RenderingHints}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import java.awt.geom.Ellipse2D

/*
 * Canvas
 * http://otfried-cheong.appspot.com/scala/drawing.html
 * http://stackoverflow.com/questions/2658554/using-graphics2d-to-overlay-text-on-a-bufferedimage-and-return-a-bufferedimage
 */
object Canvas {
  def renderSquare(width: Int): Array[Byte] = {
    val canvas = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    // clear background
    g.setColor(Color.GRAY)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
    // done with drawing
    g.dispose()

    val baos = new ByteArrayOutputStream();
    val raster = canvas.getRaster;
    ImageIO.write(canvas, "png", baos);
    baos.flush();
    val bytes = baos.toByteArray;
    baos.close();
    bytes;
  }

  def renderRectangle(width: Int, height: Int): Array[Byte] = {
    val size = (width, height)
    // create an image
    val canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // get Graphics2D for the image
    val g = canvas.createGraphics()

    // enable anti-aliased rendering (prettier lines and circles)
    // Comment it out to see what this does!
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    // clear background
    g.setColor(Color.GRAY)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
    // done with drawing
    g.dispose()

    val baos = new ByteArrayOutputStream();
    val raster = canvas.getRaster;

    ImageIO.write(canvas, "png", baos);
    baos.flush();
    val bytes = baos.toByteArray;
    baos.close();
    bytes;
  }

  def renderCircle(radius: Int): Array[Byte] = {
    // create an image
    val canvas = new BufferedImage(radius, radius, BufferedImage.TYPE_INT_RGB)
    val circle = new Ellipse2D.Float(0, 0, radius, radius);
    // get Graphics2D for the image
    val g = canvas.createGraphics()

    // enable anti-aliased rendering (prettier lines and circles)
    // Comment it out to see what this does!
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    // clear background
    g.setColor(Color.GRAY)
    g.fill(circle)
    // done with drawing
    g.dispose()

    val baos = new ByteArrayOutputStream();
    val raster = canvas.getRaster;

    ImageIO.write(canvas, "png", baos);
    baos.flush();
    val bytes = baos.toByteArray;
    baos.close();
    bytes;
  }
}
