package uk.ac.ucl.cs.groovy.coursework.jogl.maths

/**
 * <p>Representation of a mathematical vector in a 3D environment. This class
 * provides various methods to deal with mathematical vectors (e.g. a method
 * to calculate the cross product of two vectors, etc.)</p>
 *
 */
def class Vector {

  /** the X coordinate of this vector   */
  float x

  /** the Y coordinate of this vector   */
  float y

  /** the Z coordinate of this vector   */
  float z

  // -------------------------------------------- Constructors

  /**
   * <p>Constructs a new vector using the default coordinates (0.0, 0.0, 0.0).</p>
   *
   */
  def Vector() {
    this(0.0f, 0.0f, 0.0f)
  }

  /**
   * <p>Constructs a new vector using the given initial coordinates.</p>
   *
   */
  def Vector(float x, float y, float z) {
    this.x = x
    this.y = y
    this.z = z
  }

  // -------------------------------------------- Operator overloading methods

  // This section contains various methods that tell Groovy how this class implements
  // several operations like "+", "-" and "*". However, if you don't want to use the
  // operator approach, you can as well call the according methods on your own. Note
  // that there are methods available with a more meaningful name than "plus" or
  // "minus" (in this case, "translate").

  /**
   * <p>Translates the given vector by the given vector and returns the result.</p>
   *
   */
  def plus(vector) {
    return translate(vector)
  }

  /**
   * <p>Translates the given vector by the given vector and returns the result. However,
   * note that this method will translate the vector into the reverse direction!</p>
   *
   */
  def minus(vector) {
    return translate(vector * -1.0f)
  }

  /**
   * <p>Scales the vector by the given factor and returns the result of that operation.</p>
   *
   */
  def multiply(float factor) {
    return scale(factor)
  }

  /**
   * <p>Scales the vector by the given factor and returns the result of that operation.</p>
   *
   */
  def multiply(double factor) {
    return scale((float) factor)
  }

  /**
   * <p>Calculates the cross product of this and the given vector and returns the result.</p>
   *
   */
  def multiply(Vector vector) {
    return cross(vector)
  }

  // -------------------------------------------- Public methods

  /**
   * <p>Translates this vector by the given vector and returns the result, i.e. this method
   * doesn't modify this vector it rather returns the result of that operation.</p>
   *
   */
  def translate(vector) {
    return new Vector(
            (float) (getX() + vector.getX()),
            (float) (getY() + vector.getY()),
            (float) (getZ() + vector.getZ()))
  }

  /**
   * <pCalculates the cross product of this and the given vector and returns the result, i.e.
   * this method doesn't modify this vector it rather returns the result of that operation.</p>
   *
   */
  def cross(vector) {
    return new Vector(
            (float) (getY() * vector.getZ() - getZ() * vector.getY()),
            (float) (getZ() * vector.getX() - getX() * vector.getZ()),
            (float) (getX() * vector.getY() - getY() * vector.getX())
    )
  }

  /**
   * <p>Scales the vector by the given factor and returns the result of that operation.
   * Note that this method doesn't change </p>
   *
   */
  def scale(factor) {
    return new Vector(
            (float) (getX() * factor),
            (float) (getY() * factor),
            (float) (getZ() * factor)
    );
  }

  /**
   * <p>Returns the length of this vector (i.e. pythagoras' sentence
   * over the three coordinates x, y and z).</p>
   *
   */
  def length() {
    return (float) Math.sqrt(
            getX() * getX() + getY() * getY() + getZ() * getZ())
  }

  /**
   * <p>Normalizes this vector, i.e. it scales the vector in a way
   * that it has a length of 1 after this operation.</p>
   *
   */
  def normalize() {
    return scale(1 / length())
  }

  // -------------------------------------------- Object methods

  /**
   * <p>Returns a string representation of the current vector. This method is
   * mainly used for debugging purposes as thanks to this method it is more or
   * less easy to get the important information by just looking at the string
   * representation of a vector (e.g. by using println statements).</p>
   *
   */
  def String toString() {
    return "3D Vector[x: '${x}', y: '${y}', z: '${z}']"
  }

}