package uk.ac.ucl.cs.groovy.coursework.maze


/**
 * <p>Represents a single cell within a maze and provides all the information
 * regarding walls within mazes. Note that, if you change anything regarding
 * walls you'll also have to make the according changes to the neighbour cell
 * otherwise you'll end up with an inconsistent system (a single cell doesn't
 * know anything about its neighbour cells after all so it can't provide that
 * behaviour).</p>
 *
 */
def class Cell {

  /** The coordinates of this cell within the maze.   */
  def x, y

  /** A table that indicates the walls around this cell.   */
  def walls = [:]

  // -------------------------------------------- Constructors

  /**
   * <p>Constructs a new cell in a maze using the coordinates
   * for it within that maze.</p>
   */
  def Cell(x, y) {
    this.x = x
    this.y = y
  }

  // -------------------------------------------- Public methods

  /**
   * <p>Places a wall around this cell at the given direction (i.e. either
   * north, south, ..). Note that placing a wall just means that it will be
   * saved that there is a wall, i.e. this method doesn't draw anything!</p>
   */
  def void placeWall(direction) {
    walls[direction] = true
  }

  /**
   * <p>Removes the wall around this cell at the given direction (i.e.
   * either north, south, ..).</p>
   */
  def void removeWall(direction) {
    walls[direction] = false
  }

  /**
   * <p>Returns whether there is a wall around this cell at the given
   * direction (i.e. either north, south, ...).</p>
   */
  def boolean hasWall(direction) {
    walls[direction]
  }

}