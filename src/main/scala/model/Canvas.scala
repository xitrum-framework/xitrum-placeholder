package placeholder.model

import java.awt.{Color,RenderingHints}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import javax.imageio.ImageIO

object Canvas {
  //http://otfried-cheong.appspot.com/scala/drawing.html
  def render(width: Int, height: Int): Array[Byte] = {
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
}
