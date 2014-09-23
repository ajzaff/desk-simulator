package com.alanjz.desksim

import java.awt.Toolkit
import java.awt.event.{KeyEvent, KeyListener}
import javax.swing.{JFrame, SwingUtilities}

import com.alanjz.desksim.parser.Parser

object App extends App with Runnable with KeyListener {
  val frame = new JFrame("Desk Simulator 2014, Alan Zaffetti")
  val workerThread = new Thread(this)
  val appDimensions = Toolkit.getDefaultToolkit().getScreenSize()

  val fps = 60

  def width = frame.getWidth
  def height = frame.getHeight
  def tick = System.nanoTime

  private def initUI {
    frame.setSize (appDimensions)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane.add(View)
    frame.setLocationRelativeTo(null)
    frame.setUndecorated(true)
    frame.setVisible(true)

    frame.addKeyListener(this)
  }

  private def update {
    Universe.things.foreach(_.update)
  }

  private def initThings {
    // TODO: add things to play with
  }

  override def run {
    val sleepTime = 1000000000 / fps
    while(true) {
      val frameStart = tick
      frame.repaint()
      update
      while(tick - frameStart < sleepTime) {
        frame.repaint()
        Thread.sleep(0,1)
      }
    }
  }

  SwingUtilities.invokeLater(new Runnable {
    override def run(): Unit = {
      initUI
      initThings
      workerThread.start
    }
  })

  override def keyTyped(e: KeyEvent): Unit = {

  }

  override def keyPressed(e: KeyEvent): Unit ={
    if(!CmdLabel.isUserMode) {
      CmdLabel.setText("")
      CmdLabel.setUserMode()
    }
    if(e.getKeyChar.isLetter || e.getKeyCode==KeyEvent.VK_SPACE) {
      CmdLabel.setText(CmdLabel.getText() + e.getKeyChar)
    }
    else if(e.getKeyCode == KeyEvent.VK_BACK_SPACE || e.getKeyCode==KeyEvent.VK_DELETE) {
      CmdLabel.setUserMode()
      if(!CmdLabel.getText.isEmpty)
        CmdLabel.setText(CmdLabel.getText.substring(0,CmdLabel.getText.length-1))
    }
    else if(e.getKeyCode == KeyEvent.VK_ENTER) {
      val txt = CmdLabel.getText
      CmdLabel.setText("")
      CmdLabel.setAiMode()
      Parser.parseLine(txt)
    }
  }

  override def keyReleased(e: KeyEvent): Unit = {

  }
}