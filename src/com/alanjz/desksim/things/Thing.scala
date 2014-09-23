package com.alanjz.desksim.things

import java.awt.{Color, Graphics2D, Rectangle}

import com.alanjz.desksim.Universe
import com.alanjz.desksim.Universe.things

import scala.annotation.tailrec

/**
 * Created by alan on 9/12/14.
 */
trait Thing {
  val color : Color
  val width : Int
  val height : Int
  var x : Float
  var y : Float
  val id : Long
  def dimensions = (width,height)
  def point = (x,y)
  val bounds = new Rectangle(x.toInt,y.toInt,width,height)
  def paint(g2d : Graphics2D)
  def update

  def topper : Option[Thing] =
    things.find(t => (t.y + t.height == y) && (t.x == x))

  @tailrec
  final def putTopper(t : Thing) : Unit = {
    if(this.eq(t)) return
    if (!topper.isDefined) {
      t.y = y - t.height
      t.x = x
    }
    else {
      topper.get.putTopper(t)
    }
  }
  def isFloaty = y != Universe.deskLine

}