package uk.ac.ucl.cs.groovy.coursework.jogl.support

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

/**
 * <p>A window-event listener class that just listens to the 'windowClosed' event, hence
 * the name ClosedListener. Anyhow, this class somehow wraps a given closure so that
 * you can register it as a window-event listener (again, this class only listens to
 * the 'windowClosed' event so the closure will only be called once the window has
 * been closed).</p>
 *
 */
def class ClosedListener extends WindowAdapter {

  /**
   * <p>The closure that will be called as callback once the user has closed the window.</p>
   */
  def callback

  // -------------------------------------------- Constructors

  /**
   * <p>Constructs a new ClosedListener using the given closure as callback.</p>
   *
   * @param callback the closure that will be called as callback once the user has closed the window
   */
  def ClosedListener(callback) {
    this.callback = callback
  }

  // -------------------------------------------- WindowAdapter methods

  /**
   * <p>Callback method that will be invoked when a window has been closed.
   * Basically it just delegates this callback to the closure that has
   * been passed as argument to the constructor of this object.</p>
   *
   */
  def void windowClosed(WindowEvent e) {
    callback(e)
  }

}