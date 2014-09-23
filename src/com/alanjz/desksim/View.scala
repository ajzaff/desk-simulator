package com.alanjz.desksim

import java.awt.{BorderLayout, Color, Graphics, Graphics2D}
import javax.swing.JPanel
import Universe.deskLine

/**
 * Created by alan on 9/12/14.
 */
object View extends JPanel {
  val roomColor1 = new Color(12,12,45)
  val roomColor2 = new Color(255-12,255-12,255-45)
  val deskColor1 = new Color(188,188,245)
  val deskColor2 = new Color(255-188,255-188,255-245)

  var roomColor = roomColor2
  var deskColor = deskColor2

  setLayout(new BorderLayout())
  add(CmdLabel, BorderLayout.PAGE_END)

  setBackground(roomColor1)
  override def paint(g : Graphics): Unit = {
    val g2d = g.asInstanceOf[Graphics2D]
    super.paint(g2d)
    paintRoom(g2d)
    Universe.things.foreach(_.paint(g2d))
    paintComponents(g2d)
  }

  private def paintRoom(g2d : Graphics2D): Unit = {
    g2d.setColor(roomColor)
    g2d.fillRect(0,0,App.width,App.height)
    g2d.setColor(deskColor)
    g2d.drawLine(0,deskLine,App.width,deskLine)
  }

  def lightsOn = {
    roomColor=roomColor2
    deskColor=deskColor2
    CmdLabel.update()
  }

  def lightsOff = {
    roomColor=roomColor1
    deskColor=deskColor1
    CmdLabel.update()
  }
}
