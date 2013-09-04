package placeholder.model

import java.awt.Color

import com.martiansoftware.jsap.stringparsers.ColorStringParser

class Shape(color: String = "GRAY", text: String = "placeholder", textcolor: String = "WHITE") {
  val parser = ColorStringParser.getParser()
  def getColor: Color =
    parser.parse(color).asInstanceOf[Color]
  def getText: String =
    text
  def getTextColor: Color =
    parser.parse(textcolor).asInstanceOf[Color]
}

class Square(color: String, text: String, textcolor: String, width: Int)
  extends Shape(color, text, textcolor) {
  def getWidth: Int =
    width
}

class Rectangle(color: String, text: String, textcolor: String, width: Int, height: Int)
  extends Square(color, text, textcolor, width) {
  def getHeight: Int =
    height
}

class Circle(color: String, text: String, textcolor: String, radius: Int)
  extends Shape(color, text, textcolor) {
  def getRadius: Int =
    radius
}
