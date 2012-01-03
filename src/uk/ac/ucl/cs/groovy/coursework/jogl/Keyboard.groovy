package uk.ac.ucl.cs.groovy.coursework.jogl

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

/**
 *
 *
 */
public class Keyboard extends KeyAdapter {

  /**
   * This table contains the information whether a specific keyboard key
   * is currently pressed or not. Use the key code as the key of this map
   * to get a boolean value that equals true if the given key is currently
   * pressed.
   */
  private def keys = [:]

  // -------------------------------------------- Public methods

  def boolean isKeyPressed(keyCode) {
    def keyPressed = keys[keyCode] as boolean
    if (keyPressed == null) {
      return false
    } else {
      return keyPressed
    }
  }

  def void reset() {
    keys.clear()
  }

  // -------------------------------------------- KeyListener methods

  /**
   * Callback method that will be called if the user has pressed a key on
   * the keyboard. The given event parameter contains the information to
   * determine which key has been pressed.
   *
   */
  def void keyPressed(KeyEvent e) {
    // Remember that this key is pressed by setting the according table entry to true.
    keys[e.getKeyCode()] = true
  }

}