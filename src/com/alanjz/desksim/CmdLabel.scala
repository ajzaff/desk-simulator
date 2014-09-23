package com.alanjz.desksim

import java.awt.event.{KeyEvent, KeyListener}
import javax.swing.{JLabel, JTextField}
import java.awt.Font
import java.awt.Color

/**
 * Created by Alan on 9/13/14.
 */
object CmdLabel extends JLabel {

  var isUserMode = true
  def setUserMode() = {
    isUserMode = true
    setForeground(View.deskColor)
  }

  def setAiMode() = {
    isUserMode = false
    setForeground(new Color(197,28,216))
  }

  def msg(s : String) =
    setText(s":$s")

  def update() {
    setBorder(null)
    setBackground(View.roomColor)
    setForeground(View.deskColor)
    setFont(new Font("verdana", Font.PLAIN, 26))
  }

  update()
}