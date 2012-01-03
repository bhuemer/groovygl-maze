package uk.ac.ucl.cs.groovy.coursework.jogl.support

import javax.media.opengl.GL
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener
import javax.media.opengl.glu.GLU

/**
 *
 *
 */
def class JoglApplicationEventListener implements GLEventListener {

  def renderer
  def keyboard

  // -------------------------------------------- Constructors

  def JoglApplicationEventListener(renderer, keyboard) {
    this.renderer = renderer
    this.keyboard = keyboard
  }

  // -------------------------------------------- GLEventListener methods

  def void init(GLAutoDrawable drawable) {
    def gl = drawable.getGL()
    gl.glEnable(GL.GL_TEXTURE_2D)
    gl.glShadeModel(GL.GL_SMOOTH)

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    gl.glClearDepth(1.0f)

    gl.glEnable(GL.GL_DEPTH_TEST)
    gl.glDepthFunc(GL.GL_LEQUAL)
  }

  /**
   *
   */
  def void reshape(GLAutoDrawable drawable, int x, int y, int width,
                   int height) {
    def gl = drawable.getGL()
    def glu = new GLU()

    gl.glViewport(0, 0, width, height)  //Use the whole window for rendering

    // Set up a new projection matrix. The projection matrix is responsible for determining how to draw the
    // 3D scene on the 2D window that the user will see. For example, the projection matrix somehow determines
    // how far the camera can see, etc..
    gl.glMatrixMode(GL.GL_PROJECTION)
    gl.glLoadIdentity()
    glu.gluPerspective(45.0, (float) width / (float) height, 0.1, 100.0)

    // Reset the model view matrix by replacing it with an identity matrix.
    gl.glMatrixMode(GL.GL_MODELVIEW)
    gl.glLoadIdentity()
  }

  /**
   * <p>Callback method that will be called once the application is supposed to render the scene. Thanks
   * to a background thread (the FPSAnimator class is responsible for that), whis method will be called
   * like 60 times per second!</p>
   *
   */
  def void display(GLAutoDrawable drawable) {
    renderer.call(drawable, keyboard)
    keyboard.reset()
  }

  /**
   *
   */
  def void displayChanged(
  GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    // ...
  }

}