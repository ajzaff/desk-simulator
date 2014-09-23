package com.alanjz.desksim.parser

import java.awt.{Rectangle,Color}

import com.alanjz.desksim.{View, CmdLabel}
import com.alanjz.desksim.things.{Thing, Cube}

import com.alanjz.desksim.Universe.{deskLine,things}

import scala.util.Random

import com.alanjz.desksim.CmdLabel.msg

object Parser {
  val makeNew = Set(
    "make","build","construct","spawn","add","create"
  )
  val remove = Set(
    "remove","delete","purge","erase","destroy","eliminate"
  )
  val stack = Set(
    "stack","place","onto","top"
  )

  def removeAll(colorKey : Option[String], sizeKey : Option[String]) = {
    var f = things
    if(colorKey.isDefined) {
      f = f.filter(_.color.eq( colors(colorKey.get) ))
    }
    if(sizeKey.isDefined) {
      val sz = sizeModifiers(sizeKey.get)
      f = f.filter(o=> o.dimensions._1==sz && o.dimensions._2==sz)
    }
    f.foreach(things.remove(_))
  }

  def removeAllExcept(colorKey : Option[String], sizeKey : Option[String]) = {
    var f = things
    if(colorKey.isDefined) {
      f = f.filter(_.color.eq( colors(colorKey.get) ))
    }
    if(sizeKey.isDefined) {
      val sz = sizeModifiers.get(sizeKey.get)
      f = f.filter(_.dimensions == (sz,sz))
    }
    for(o <- things) {
      if(!f.contains(o)) things.remove(o)
    }
  }

  val colors = Map(
    "red" -> Color.red,
    "green" -> Color.green,
    "blue" -> Color.blue,
    "yellow" -> Color.yellow,
    "orange" -> Color.orange,
    "cyan" -> Color.cyan,
    "mageta" -> Color.magenta,
    "pink" -> Color.pink
  )

  val sizeModifiers = Map(
    "small" -> 30,
    "tiny" -> 20,
    "normal" -> 50,
    "big" -> 100,
    "medium" -> 50,
    "large" -> 100,
    "huge" -> 200,
    "giant" -> 200
  )

  val objects = Map(
    "box" -> Cube,
    "cube" -> Cube,
    "block" -> Cube,
    "square" -> Cube
  )

  def parseLine(str : String) : Unit = {
    val words = str.split(' ').map(_.toLowerCase).toSeq
    val makeFlag = words.exists(makeNew.contains(_))
    val removeFlag = words.exists(remove.contains(_))
    val stackFlag = words.exists(stack.contains(_))
    val colorFlag = words.find(colors.contains(_))
    val obj = words.find(objects.contains(_))
    val size = words.find(sizeModifiers.contains(_))

    // Determine the color of the object
    val cIndex = Random.nextInt(colors.values.size)
    var c = colors.values.toStream.drop(cIndex-1).head
    if(colorFlag.isDefined) {
      c = colors(colorFlag.get)
    }

    // Determine the size of the object
    var sz = 50
    if(size.isDefined) {
      sz = sizeModifiers(size.get)
    }

    // creating a new object
    if(makeFlag) {

      // If no object return bogus
      if(!obj.isDefined) {
        CmdLabel.setText("Huh?")
        return
      }

      // debug
      msg(s"creating a $size $colorFlag $obj")

      // Add the object to the Universe
      val t = objects(obj.get).apply(c,sz)
      things.add( t )
    }

    // removing existing objects
    else if (removeFlag) {

      // determine the target of remsoval
      if(words.exists(_=="but") || words.exists(_=="except")) {
        msg(s"remove all except $colorFlag $size")
        removeAllExcept(colorFlag,size)
      }
      else {
        msg(s"remove all $colorFlag $size")
        removeAll(colorFlag,size)
      }

      // so here we go. when removing blocks we need to run stack fix to simulate gravity
      stackFix()
    }

    // stacking
    else if (stackFlag) {

      // the top properties
      val topColor = words.find(colors.contains(_))
      val topSize = words.find(sizeModifiers.contains(_))

      // the bottom properties
      val bottomColor = words.reverse.find(colors.contains(_))
      val bottomSize = words.reverse.find(sizeModifiers.contains(_))

      // debug ;)
      msg(s"stacking all disjoint ($topColor $topSize) on ($bottomColor $bottomSize)... ")

      // get bottom things
      var bottoms = things
      if(bottomColor.isDefined) {
        bottoms = bottoms.filter(t=> t.color.eq(colors(bottomColor.get)))
      }
      if(bottomSize.isDefined) {
        val sz = sizeModifiers(bottomSize.get)
        bottoms = bottoms.filter(t=> t.dimensions._1==sz && t.dimensions._2==sz)
      }

      // get topper things
      var tops = things
      if(topColor.isDefined) {
        tops = tops.filter(t=> t.color.eq(colors(topColor.get)))
      }
      if(topSize.isDefined) {
        val sz = sizeModifiers(topSize.get)
        tops = tops.filter(t=> t.dimensions._1==sz && t.dimensions._2==sz)
      }

      // more debug ;)
      println(s"(matches ${bottoms.size} bottom and ${tops.size} top.")
      print("bottoms: "); bottoms.foreach(s=>print(s+" ")); println
      print("tops: "); tops.foreach(s=>print(s+" ")); println;

      // do the stacking!
      for(b <- bottoms) for(t <- tops)
        b.putTopper(t)

    }

    // lights camera... ;)
    else if(words.contains("light") || words.contains("lights")) {

      // debug
      val off = words.contains("off")
      msg(s"lights " + ( if(off) "off" else "on"))

      if(words.contains("off")) {
        View.lightsOff;  }
      else {
        View.lightsOn; }
    }
  }

  def stackFix() = {
    val floaters = things.filter(_.isFloaty).toSeq.sortBy(_.y).reverse

    for(t <- floaters) {
      // see if we find any juicy things to land on in this region
      val rect = new Rectangle(t.x.toInt,t.y.toInt+t.height,t.width,deskLine-t.y.toInt+t.height)
      val bottom = things.find(t=>t.bounds.intersects(rect) || t.bounds.contains(rect))

      // land on the thing
      if(bottom.isDefined) {
        bottom.get.putTopper(t)
      }
      // land on desk
      else {
        t.y = deskLine-t.height
      }
    }
  }
}
