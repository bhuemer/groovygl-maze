package uk.ac.ucl.cs.groovy.coursework.jogl

import com.sun.opengl.util.FPSAnimator
import java.awt.BorderLayout
import java.awt.Container
import java.util.logging.Logger
import javax.media.opengl.GLCanvas
import javax.swing.JFrame
import javax.swing.JPanel
import uk.ac.ucl.cs.groovy.coursework.jogl.Keyboard
import uk.ac.ucl.cs.groovy.coursework.jogl.support.ClosedListener
import uk.ac.ucl.cs.groovy.coursework.jogl.support.JoglApplicationEventListener
import uk.ac.ucl.cs.groovy.coursework.jogl.support.JoglNativeLibraryProvider

/**
 *
 *
 */
def class JoglApplication extends JFrame {

  /**
   * The logger instance for this class.
   */
  def final static logger = Logger.getLogger(JoglApplication.class.getName())

  def Container componentPane

  /**
   * Static initialization block that loads the native libraries for the Java OpenGL classes.
   *
   */
  static {
    if (!System.getProperties().containsKey("uk.ac.ucl.provide.path")) {
      JoglNativeLibraryProvider nativeLibraryProvider = new JoglNativeLibraryProvider()
      nativeLibraryProvider.provideJoglLibraries()
    } else {
      logger.info("This application will not provide the native libraries for Java OpenGL automatically, " +
              "as you have specified the 'uk.ac.ucl.provide.path' property. Be sure that the correct " +
              "native libraries are available otherwise you will face some severe linkage errors.")
    }
  }

  // -------------------------------------------- Constructors

  def JoglApplication(String title, int width, int height) {
    setTitle(title)
    setSize(width, height)

    getContentPane().setLayout(new BorderLayout())

    componentPane = new JPanel()
    componentPane.setName("componentPane")
    componentPane.setLayout(new BorderLayout())
    getContentPane().add(componentPane, BorderLayout.SOUTH)
  }

  // -------------------------------------------- Public methods

  def void draw(int fps, renderer) {
    def keyboard = new Keyboard()

    def canvas = new GLCanvas()
    canvas.addGLEventListener(
            new JoglApplicationEventListener(renderer, keyboard))
    canvas.addKeyListener(keyboard)
    getContentPane().add(canvas, BorderLayout.CENTER)

    // Start an animation thread that redraws the GL canvas at a given rate (i.e. that's
    // the purpose of the fps parameter - frames per seconds).
    def animator = new FPSAnimator(canvas, fps)
    animator.start()

    // Add a window listener that stops the animator once the user has closed this window.
    // Otherwise the animation thread would prevent this application shutting down and the
    // user would have to forcefully quit it (e.g. by pressing Ctrl-C on the terminal or
    // using the Windows task manager).
    addWindowListener(new ClosedListener({
      animator.stop()
    }))

    setVisible(true)
    setDefaultCloseOperation(EXIT_ON_CLOSE)
  }

}