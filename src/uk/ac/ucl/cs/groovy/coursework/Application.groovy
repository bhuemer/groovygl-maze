package uk.ac.ucl.cs.groovy.coursework

import com.sun.opengl.util.texture.TextureIO
import java.awt.BorderLayout
import java.awt.event.KeyEvent
import javax.media.opengl.GL
import javax.swing.JLabel
import uk.ac.ucl.cs.groovy.coursework.jogl.Camera
import uk.ac.ucl.cs.groovy.coursework.jogl.JoglApplication
import uk.ac.ucl.cs.groovy.coursework.jogl.Keyboard
import uk.ac.ucl.cs.groovy.coursework.maze.Direction
import uk.ac.ucl.cs.groovy.coursework.maze.MazeRenderer
import uk.ac.ucl.cs.groovy.coursework.maze.generation.DepthFirstMazeGenerator

/**
 *
 *
 */
public class Application {

  /** The size of a single cell / grid in the maze.  */
  def static final GRID_SIZE = 2.0f

  /** The generator that we're using to generate new mazes.  */
  def generator

  /** The current maze we're displaying in this application.  */
  def maze

  /** The first-person-view camera of the player in the maze.  */
  def person

  /** The current camera that will be used to determine what the user sees.  */
  def camera

  /** A cache that contains all loaded textures for the 3D scene.  */
  def textures = [:]

  // -------------------------------------------- Constructors

  def Application(def generator) {
    this.generator = generator
    reinitialize()
  }

  // -------------------------------------------- Public methods

  def start() {
    def application = new JoglApplication("JOGL Application", 500, 500)

    // Add some information about how to use the application at the bottom of the window
    application.getComponentPane().add(new JLabel("- Move around using the arrow keys"), BorderLayout.NORTH)
    application.getComponentPane().add(
            new JLabel("- Toggle between first person view and bird view using the space key"), BorderLayout.CENTER)
    application.getComponentPane().add(new JLabel("- Reinitialize the application using the key 'R'"), BorderLayout.SOUTH)

    application.draw(60, {drawable, keyboard ->
      handleInput(keyboard)

      // Initialize the current frame to a black canvas
      GL gl = drawable.getGL()
      gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)

      // Place the camera in the 3D scene
      camera.update(gl)

      // Now render the whole scene
      renderGround(gl)
      renderMaze(gl)
      renderPerson(gl)
      gl.glFlush()
    })
  }

  // -------------------------------------------- Utility methods

  /**
   * <p>Handles keyboard input, for example, by moving around
   * the player in the maze if the user hits the arrow keys.</p>
   *
   */
  def handleInput(keyboard) {
    // If we're using the person's view as camera at the moment enable
    // the user to walk around in the 3D scene using the arrow keys.
    if (keyboard.isKeyPressed(KeyEvent.VK_UP)) {
      person.translate(0.0f, 0.0f, 0.1f) // Move forward
    }
    else if (keyboard.isKeyPressed(KeyEvent.VK_DOWN)) {
      person.translate(0.0f, 0.0f, -0.1f) // Move backward
    }
    if (keyboard.isKeyPressed(KeyEvent.VK_LEFT)) {
      person.rotateY(-0.1f) // Rotate to the left
    } else if (keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) {
      person.rotateY(0.1f) // Rotate to the right
    }

    // Toggle between the person's view and bird view using the space key
    if (keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
      if (camera == person) {
        camera = createBirdPerspective()
      } else {
        camera = person
      }
    }

    if (keyboard.isKeyPressed(KeyEvent.VK_R)) {
      reinitialize()
    }
  }

  /**
   * <p>Switches to a different camera from the bird's perspective so that the user can
   * take a look at the maze from above (so that he / she knows where he / she is within
   * the maze).</p>
   *
   */
  def createBirdPerspective() {
    def mazeSize = calculateMazeSize()
    def birdPerspective = new Camera()
    birdPerspective.translate(
            (float) (mazeSize / 2.0), (float) (mazeSize * 1.5), (float) (mazeSize / 2.0),
    )
    birdPerspective.rotateX(Math.PI / 2)

    return birdPerspective
  }

  def renderGround(gl) {
    withTexture("/uk/ac/ucl/cs/groovy/coursework/resources/ground.jpg", {
      def mazeSize = calculateMazeSize()

      gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
      gl.glBegin(GL.GL_POLYGON)
      // Using textures we not only have to specify the coordinates of the
      // shape we're drawing (in this case a rectangle, starting at the
      // top-left corner in a counter-clockwise direction), but also the
      // coordinates of the texture. Usually the range for those coordinates
      // is [0,1], but we've configured the texture to repeat itself if
      // we're using values outside of that range, hence we use the same
      // coordinates as for the rectangle.
      gl.glTexCoord2d(0.0d, mazeSize)
      gl.glVertex3d(0.0d, 0.0d, -mazeSize)
      gl.glTexCoord2d(0.0d, 0.0d)
      gl.glVertex3d(0.0d, 0.0d, 0.0d)
      gl.glTexCoord2d(mazeSize, 0.0d)
      gl.glVertex3d(mazeSize, 0.0d, 0.0d)
      gl.glTexCoord2d(mazeSize, mazeSize)
      gl.glVertex3d(mazeSize, 0.0d, -mazeSize)
      gl.glEnd()
    })
  }

  /**
   * <p>Renders the maze or more precisely said it renders
   * just the walls of the previously generated maze.</p>
   *
   */
  def renderMaze(gl) {
    // Load a texture for the walls of the maze and bind / unbind it properly
    withTexture("/uk/ac/ucl/cs/groovy/coursework/resources/wall.jpg", {
      def mazeSize = calculateMazeSize()
      def renderer = new MazeRenderer(maze)
      renderer.render {sx, sy, dx, dy ->
        // This closure renders a single wall which is denoted by four different
        // coordinates or two points, (sx, sy) and (dx, dy). The MazeRenderer
        // only knows about the coordinate system of the maze, so we'll have to
        // transform the coordinates appropriately. For example, in a 3D scene
        // the y-coordinate would indicate the height of a wall, which has like
        // nothing to do with the position of the wall. Hence, we'll use the
        // y-coordinate as a x-coordinate in our coordinate system, and the
        // x-coordinate will be used as a z-coordinate.
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        gl.glBegin(GL.GL_POLYGON)
        gl.glTexCoord2d(0.0d, mazeSize)
        gl.glVertex3d(sy * GRID_SIZE, 2.0f, -sx * GRID_SIZE)
        gl.glTexCoord2d(0.0d, 0.0d)
        gl.glVertex3d(sy * GRID_SIZE, 0.0f, -sx * GRID_SIZE)
        gl.glTexCoord2d(mazeSize, 0.0d)
        gl.glVertex3d(dy * GRID_SIZE, 0.0f, -dx * GRID_SIZE)
        gl.glTexCoord2d(mazeSize, mazeSize)
        gl.glVertex3d(dy * GRID_SIZE, 2.0f, -dx * GRID_SIZE)
        gl.glEnd()
      }
    })
  }

  /**
   * <p>Renders a triangle that indicates where the person is standing in the maze at the
   * moment. The top of the triangle determines the orientation of the person, i.e. it
   * determines the point that he / she is looking at. However, note that this triangle
   * will only be rendered if the user selects the bird's eye view.</p>
   *
   */
  def renderPerson(gl) {
    // Has the user selected the bird's perspective? 
    if (camera != person) {
      // If so determine the vertices of the triangle by using
      // the coordinate system of the person's camera (more or
      // less). In doing so, we'll get a neat triangle that
      // exactly represents the person's view.
      def position = person.getPosition()

      def left = position - person.getRight()
      def top = position + person.getForward() * 3
      def right = position + person.getRight()

      // Draw a red triangle
      gl.glColor3f(1.0f, 0.0f, 0.0f)
      gl.glBegin(GL.GL_POLYGON)
      gl.glVertex3d(left.getX(), left.getY(), left.getZ())
      gl.glVertex3d(top.getX(), top.getY(), top.getZ())
      gl.glVertex3d(right.getX(), right.getY(), right.getZ())
      gl.glEnd()

    }
  }

  /**
   * <p>This method reinitializes the whole application by
   * resetting the camera and regenerating the maze.</p>
   *
   */
  def reinitialize() {
    maze = generator.generateMaze(20)

    // Specify both an entry and an exit point
    maze.getCell(0, 0).removeWall(Direction.West)
    maze.getCell(19, 19).removeWall(Direction.East)

    // Create a new camera that knows where the person in the 3D scence
    // is located. Additionally initialize it with some meaningful defaults
    // (if we don't increase the y-value for example, the person's view
    // would be more or less on the floor's height).
    person = new Camera()
    person.translate(1.0f, 1.0f, -3.0f)

    // By default, we'll use the person's view!
    camera = person
  }

  /**
   * <p>Utility methods that manages textures and loads them on demand.</p>
   *
   */
  def withTexture(String filename, renderer) {
    def texture = textures[filename]
    if (texture == null) {
      def fromIndex = filename.lastIndexOf(('.' as char) as int)
      if (fromIndex > 0) {
        texture = TextureIO.newTexture(
                getClass().getResource(filename), false, filename.substring(fromIndex))
      } else {
        throw new IllegalArgumentException("The given file name must have a valid file extension.")
      }

      // Save the loaded texture in our cache for the next time.
      textures[filename] = texture
    }

    try {
      // Bind the texture to the current OpenGL context, which just means, OpenGL
      // will now use this texture if we specify the texture coordinates while
      // rendering a figure.
      texture.enable()
      texture.bind()

      // Specify that textures should be repeated if the size of the shape that
      // they're attached to, is too big (on both sides, i.e. s and t instead
      // of the usual x and y).
      texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT)
      texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT)

      // Issue the callback with the loaded texture
      renderer(texture)
    } finally {
      // Unbind the texture, otherwise the next rendering step would possibly use that texture as well.
      texture.disable()
    }
  }

  def calculateMazeSize() {
    return maze.getCells().size() * GRID_SIZE
  }

  // -------------------------------------------- Application entry method

  def static void main(String[] args) {
    Application application =
    new Application(new DepthFirstMazeGenerator())
    application.start()
  }

}