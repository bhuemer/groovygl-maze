package uk.ac.ucl.cs.groovy.coursework.maze

import uk.ac.ucl.cs.groovy.coursework.maze.Direction

/**
 * <p>A utility class that knows how to render a maze. It basically just determines all
 * available walls and calls a closure to actually draw those walls.</p>
 *
 */
public class MazeRenderer {

  /** The maze that this renderer should render  */
  def maze

  /**
   * <p>Constructs a new maze renderer using the maze that this renderer should render.</p>
   *
   */
  def MazeRenderer(maze) {
    this.maze = maze
  }

  /**
   * <p>Renders the maze by issuing "draw-wall" statements. A "draw-wall" statement
   * is basically a callback to the given closure with the coordinates of the wall
   * in the maze's coordinate system. For example, if you should draw the north
   * wall of of the cell (2,2), you'll get two points to indicate the wall:
   * (2, 2) and (3, 2). If you're using a different coordinate system, just
   * transform those values.</p>
   *
   */
  def render(Closure renderer) {
    maze.eachCell {cell ->
      // Check every direction, check if there is a wall, if so, adjust the
      // coordinates accordingly and issue a "draw-wall" statement by calling
      // the given closure.
      if (cell.hasWall(Direction.North)) {
        renderer(cell.getX(), cell.getY(),
                cell.getX() + 1, cell.getY())
      }
      if (cell.hasWall(Direction.South)) {
        renderer(cell.getX(), cell.getY() + 1,
                cell.getX() + 1, cell.getY() + 1)
      }
      if (cell.hasWall(Direction.West)) {
        renderer(cell.getX(), cell.getY(),
                cell.getX(), cell.getY() + 1)
      }
      if (cell.hasWall(Direction.East)) {
        renderer(cell.getX() + 1, cell.getY(),
                cell.getX() + 1, cell.getY() + 1)
      }
    }
  }

}