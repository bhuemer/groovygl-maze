package uk.ac.ucl.cs.groovy.coursework.maze.generation

import uk.ac.ucl.cs.groovy.coursework.maze.Direction
import uk.ac.ucl.cs.groovy.coursework.maze.Maze
import uk.ac.ucl.cs.groovy.coursework.maze.generation.MazeGenerator

/**
 * <p>A maze generator that uses a generation algorithm that is based on the depth-first
 * search traversal algorithm. How the algorithm works is described on Wikipedia, see
 * <a>http://en.wikipedia.org/wiki/Maze_generation_algorithm#Depth-first_search</a>.</p>
 *
 */
def class DepthFirstMazeGenerator implements MazeGenerator {

  /**
   * <p>Generator object for random numbers. This algorithm has to
   * decide which neighbour to choose randomly (otherwise we
   * would always end up with the same maze being generated).</p>
   */
  def random = new Random()

  // -------------------------------------------- Public methods

  /**
   * <p>Generates a random maze of the given size using a depth-first traversal based
   * algorithm. However, note that this method specifies neither the maze's entry nor
   * its exit point. You'll have to specify those points on your own after the maze
   * has been generated.</p>
   *
   */
  def generateMaze(size) {
    // Create a new maze of the given size and initialize the cells accordingly.
    // This algorithm assumes that there are only walls at first, it will gradually
    // break down some walls.
    def maze = new Maze(size)
    maze.eachCell {cell ->
      cell.placeWall(Direction.North)
      cell.placeWall(Direction.East)
      cell.placeWall(Direction.South)
      cell.placeWall(Direction.West)
    }

    // Choose a random cell as the starting point and start generating the maze.
    return generateMaze(maze, [:], maze.getCell(
            random.nextInt(size), random.nextInt(size)
    ))
  }

  // -------------------------------------------- Utility methods

  /**
   * <p>Generates a maze or more precisely said it takes the given maze and removes as many
   * walls as possible using a depth-first traversal algorithm, i.e. this method assumes that
   * the given maze consists only of cells with four surrounding walls at first.</p>
   *
   * <p>Note that this method specifies neither the maze's entry nor its exit point. You
   * can do on your own though after the maze has been generated.</p>
   */
  private def generateMaze(maze, visitedCells, currentCell) {
    // Mark the current cell as visited.
    visitedCells[currentCell] = true

    def directions = Direction.values()

    // While we haven't checked all neighbours yet ..
    def visitedNeighbourDirections = [] // This set (or technically this list) contains all the directions we've visited so far.
    while (visitedNeighbourDirections.size() < directions.length) {
      // .. select a direction randomly in order to determine the next neighbour.
      // Note that we don't care about whether we've visited that neighbour
      // in this case as the purpose of these statements is just to ensure
      // that each neighbour will at least be considered in a random order
      // though. 
      def randomDirection = directions[random.nextInt(directions.length)]
      if (!visitedNeighbourDirections.contains(randomDirection)) {
        visitedNeighbourDirections.add(randomDirection)

        // Retrieve the neighbour cell at that particular direction.
        def neighbourCell =
          maze.getCellNeighbour(currentCell, randomDirection)
        // Have we visited this particular cell alreday? (Note that the neighbour
        // cell equals null in the case of there is no neighbour cell at that
        // direction, i.e. the current cell is already the outmost cell)
        if (neighbourCell != null && !visitedCells.containsKey(neighbourCell)) {
          // If we haven't visited that neighbour cell, well
          // then break down the wall and visit that cell!

          // Remove the wall. We'll have to tell both cells that the wall has been
          // removed. The neighbour cell obviously needs to know the reverse
          // direction (i.e. if it's the south neighbour to the current cell, then
          // the neighbour cell has to remove the north wall).
          currentCell.removeWall(randomDirection)
          neighbourCell.removeWall(randomDirection.reverse())

          // Continue with the generation at the neighbour cell (depth-first!)
          generateMaze(maze, visitedCells, neighbourCell)
        }
      }
    }

    return maze
  }

}