package uk.ac.ucl.cs.groovy.coursework.maze

import uk.ac.ucl.cs.groovy.coursework.maze.Cell
import uk.ac.ucl.cs.groovy.coursework.maze.Direction

/**
 * <p>Represents a maze, i.e. it represents a two-dimensional data
 * structure of maze cells and provides some convenience methods to
 * access those cells.</p>
 *
 */
def class Maze {

  /** A two-dimensional list of all the cells in this maze.  */
  def cells

  // -------------------------------------------- Constructors

  /**
   * <p>Constructs a new maze using the given size. Note that the size
   * indicates both the number of rows and the number of columns of the
   * maze.</p>
   *
   */
  def Maze(int size) {
    cells = []
    size.times {x ->
      cells[x] = []
      size.times {y ->
        cells[x][y] = new Cell(x, y)
      }
    }
  }

  // -------------------------------------------- Public methods

  /**
   * <p>Iteration method that calls the given visitor for each cell in the maze.</p>
   *
   */
  def eachCell(visitor) {
    def size = cells.size()
    size.times {x ->
      size.times {y ->
        visitor(getCell(x, y))
      }
    }
  }

  /**
   * <p>Returns the cell at the given position.</p>
   */
  def getCell(x, y) {
    return cells[x][y]
  }

  /**
   * <p>Returns the neighbour cell of the given cell at the given direction, or
   * <code>null</code> if there is no neighbour cell at the given direction (i.e.
   * the given cell is already the outmost cell).</p>
   *
   */
  def getCellNeighbour(cell, direction) {
    def x = cell.getX()
    def y = cell.getY()

    switch (direction) {
      case Direction.North: y--; break;
      case Direction.East: x++; break;
      case Direction.South: y++; break;
      case Direction.West: x--; break;
    }

    if (x < 0 || y < 0 ||
            x >= cells.size() || y >= cells.size()) {
      return null
    } else {
      return getCell(x, y)
    }
  }

}