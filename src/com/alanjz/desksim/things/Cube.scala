package com.alanjz.desksim.things

import com.alanjz.desksim.App
import com.alanjz.desksim.Universe.{things,deskLine}

import java.awt.{Rectangle, Color, Graphics2D}

import scala.util.Random
/**
 * Created by alan on 9/12/14.
 */
class Cube(var x : Float, var y : Float, val size : Int, val color : Color) extends Thing {
  val width = size
  val height = size
  val id = Random.nextLong
  override def paint(g2d : Graphics2D): Unit = {
    g2d.setColor(color)
    g2d.fillRect(x.toInt,y.toInt,width,height)
    g2d.setColor(color.darker.darker)
    g2d.drawRect(x.toInt,y.toInt,width,height)
  }
  override def update: Unit = {
  }

  override def toString =
    s"cube(x=$x y=$y size=$size color=$color)"

}

object Cube {
  def apply(color : Color, size : Int) : Cube = {

    def endTerm(o : Thing, x : Int, y : Int) =
      o.bounds.intersects(new Rectangle(x,y,size,size)) ||
      o.bounds.contains(new Rectangle(x,y,size,size))

    val y = deskLine - size
    var x = 0
    do {
      x = Random.nextInt(App.width-size)
    } while(things.exists(o=> endTerm(o,x,y)))
    new Cube(x,y,size,color)
  }
}