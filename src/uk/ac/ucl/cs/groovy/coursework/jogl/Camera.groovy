package uk.ac.ucl.cs.groovy.coursework.jogl

import javax.media.opengl.GL
import javax.media.opengl.glu.GLU
import uk.ac.ucl.cs.groovy.coursework.jogl.maths.Vector

/**
 *
 *
 */
public class Camera {

  def position

  def forward
  def right
  def up

  // -------------------------------------------- Constructors

  def Camera() {
    reset()
  }

  // -------------------------------------------- Public methods

  def reset() {
    position = new Vector(0.0f, 0.0f, 0.0f)

    forward = new Vector(0.0f, 0.0f, -1.0f)
    right = new Vector(1.0f, 0.0f, 0.0f)
    up = new Vector(0.0f, 1.0f, 0.0f)
  }

  def rotateX(roll) {
    // Rotate around the X-axis
    // Note that if the roll angle is greater than 0, we actually have to move the
    // forward vector downwards, hence the subtraction in the following statement.
    forward = (forward * Math.cos(roll) - up * Math.sin(roll)).normalize()
    up = forward * right // Up is just the cross product of forward and right in this case  
  }

  def rotateY(pitch) {
    // Rotate around the Y-axis
    forward = (forward * Math.cos(pitch) + right * Math.sin(pitch)).normalize()
    right = forward * up // Up doesn't chang, forward has been rotated already -> the cross product again
  }

  def rotateZ(yaw) {
    // Rotate around the Z-axis
    right = (right * Math.cos(yaw) - up * Math.sin(yaw)).normalize()
    up = forward * right
  }

  def translate(x, y, z) {
    position = position + right * x
    position = position + up * y
    position = position + forward * z
  }

  def update(gl) {
    // Reset the current modelview matrix, i.e. the matrix that
    // determines the position of the camera more or less.
    gl.glMatrixMode(GL.GL_MODELVIEW)
    gl.glLoadIdentity()

    // The GLU lookAt method requires you to specify a point where the
    // camera should look at as the second parameter. We'll just have
    // to take the current position and go one step forward to get
    // that parameter.
    def lookAt = position + forward

    GLU glu = new GLU()
    glu.gluLookAt(
            position.getX(), position.getY(), position.getZ(),
            lookAt.getX(), lookAt.getY(), lookAt.getZ(),
            up.getX(), up.getY(), up.getZ()
    )
  }

}