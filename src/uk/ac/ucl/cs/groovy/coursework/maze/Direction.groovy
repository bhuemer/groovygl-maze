package uk.ac.ucl.cs.groovy.coursework.maze

/**
 * <p>An enumeration of all possible directions (i.e. north, south, ...).</p>
 */
def enum Direction {

  North,
  East,
  South,
  West;

  /**
   * <p>Returns the opposite direction depending on what this direction is.</p>
   */
  def reverse() {
    switch (this) {
      case North: return South
      case South: return North

      case West: return East
      case East: return West
    }
  }

}