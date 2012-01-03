package uk.ac.ucl.cs.groovy.coursework.maze.generation

/**
 * <p>Strategy interface for a maze generation algorithm.</p>
 *
 */
def interface MazeGenerator {

  /**
   * <p>Generates a random maze of the given size using a depth-first traversal based
   * algorithm. However, note that this method specifies neither the maze's entry nor
   * its exit point. You'll have to specify those points on your own after the maze
   * has been generated.</p>
   *
   */
  def generateMaze(size)

}