package uk.ac.ucl.cs.groovy.coursework.jogl.support

import java.util.logging.Level
import java.util.logging.Logger

def class JoglNativeLibraryProvider {

  /**
   * A list of the native libraries that have to be provided so that a Java OpenGL application works.
   */
  def static NATIVE_LIBRARIES = ['gluegen-rt', 'jogl', 'jogl_awt', 'jogl_cg']

  /**
   * The logger instance for this class.
   */
  def static logger = Logger.getLogger(JoglNativeLibraryProvider.class.getName())

  /**
   * <p>Configures the current JVM so that it knows how to find the native libraries for
   * the Java OpenGL library. Otherwise the user would have to set the 'java.library.path'
   * property manually each time.</p>
   *
   */
  def provideJoglLibraries() {
    def nativeLibraryPath = determineLibraryPath()
    if (nativeLibraryPath != null) {
      // Use the current class as the starting point for resource loading.
      def nativeLibraryURL = getClass().getResource(nativeLibraryPath)
      if (nativeLibraryURL == null) {
        throw new IllegalStateException(
                "Couldn't find the directory containing the packaged native library files. Someone has probably " +
                        "modified this application, i.e. e.g. he/she has modified the JAR file?")
      }

      if ('file'.equals(nativeLibraryURL.getProtocol())) {
        appendLibraryPath(nativeLibraryURL.getFile())
      } else {
        // In this case just copy the library files to a temporary directory
        def tempDir = null;
        while (tempDir == null || tempDir.exists()) {
          // Choose a more or less random directory within the temporary directory
          String randomFolder = "groovy-coursework-" + Integer.toString((int) (Math.random() * 1000));
          tempDir = new File(System.getProperty("java.io.tmpdir"), randomFolder);
        }

        // Create the temporary directory and tell the VM to delete it once the application shuts down.
        tempDir.mkdir()
        tempDir.deleteOnExit()

        NATIVE_LIBRARIES.each {nativeLibrary ->
          def platformDependentFileName = System.mapLibraryName(nativeLibrary);
          // Retrieve a URL to the native library by using the class
          // resource loading approach again, but this time we'll have
          // to pass the platform-dependent version of the library name.
          nativeLibraryURL = getClass().getResource(
                  nativeLibraryPath + platformDependentFileName)
          if (nativeLibraryURL == null) {
            throw new IllegalStateException(
                    "Couldn't find the library file for the native library '${nativeLibrary}'. " +
                            "Someone has probably modified this application, i.e. he/she has modified the JAR file?")
          }

          try {
            copy(nativeLibraryURL,
                    new File(tempDir, platformDependentFileName))
          } catch (IOException ex) {
            logger.log(Level.SEVERE, "An error occured while trying to copy the " +
                    "native library files to a temporary directory.", ex)
          }
        }

        // Register our temporary directory as a native library path element
        appendLibraryPath(tempDir.getAbsolutePath())
      }
    }
  }

  /**
   * <p>Utility method that returns the path to the native library files
   * within the classpath. The returned path is in fact a URL object, or
   * <code>null</code> if there are no libraries packaged for the current
   * operating system.</p>
   *
   */
  def private static determineLibraryPath() {
    def libraryPath

    // Depending on the current operating system we have to return a different
    // library path. I could have as well stored all those different library
    // versions within a single directory, but for the sake of security I
    // chose to differ between the different operating systems. In doing so,
    // I'm sure that the correct version will be loaded.
    // Note that I'm ignoring the case at this point, which is why I only want
    // to deal with lower case strings. 
    def operatingSystem = System.getProperty('os.name').toLowerCase()
    if (operatingSystem.contains('linux')) {
      libraryPath = '/uk/ac/ucl/cs/groovy/coursework/jogl/support/libraries/linux/'
    } else if (operatingSystem.contains('windows')) {
      libraryPath = '/uk/ac/ucl/cs/groovy/coursework/jogl/support/libraries/windows/'
    } else if (operatingSystem.contains('mac os x')) {
      libraryPath = '/uk/ac/ucl/cs/groovy/coursework/jogl/support/libraries/macosx/'
    } else {
      logger.warning(
              "No native libraries are packaged for your operating system '${operatingSystem}'. " +
                      "You'll have to specify them manually using the environment variable 'java.library.path'.")
      libraryPath = null
    }

    return libraryPath
  }

  /**
   * <p>Utility method that appends the given library path to the 'java.library.path' property.
   * That means, after that method call Java will also look in the given library path if it wants
   * to retrieve a native JNI library.</p>
   */
  def private static appendLibraryPath(libraryPath) {
    // Unfortunately there's no way to modify the 'java.library.path' property at
    // runtime. Even if we actually call System.setProperty('java.library.path', ..)
    // it doesn't affect the JNI loading/linking process as it's a read-only
    // property. What we have to do in this case is to work around this issue by
    // digging through the sourcecode of the class 'java.lang.ClassLoader'. There's
    // a static field called 'usr_paths' that more or less equals the path to use
    // for the JNI loading/linking process. Thanks to Java Reflection it's possible
    // to modify that field! However, notet that this is somehow a hack and is only
    // guaranteed to work on a Sun JVM (it doesn't crash on other VMs though, you
    // just have to specify the libraries manually again).
    try {
      def field = ClassLoader.class.getDeclaredField('usr_paths')
      field.setAccessible(true)

      def libraryPaths = []

      // Append all previously known library paths at first ..
      libraryPaths.addAll(Arrays.asList(field.get(null) as String[]))

      // Only add the custom library path at the end, if it's not already included anyway.
      if (!libraryPaths.contains(libraryPath)) {
        // .. and then add our custom native library path at the end, i.e.
        // we're basically just building the union of two sets in this case.
        libraryPaths.add(libraryPath)
      }

      // Write back the changes we've made to the static field (hence we're using
      // null as the first argument as there's no object instance in this case).
      field.set(null, libraryPaths as String[])

      if (logger.isLoggable(Level.INFO)) {
        logger.info("Using the following library paths now: '${libraryPaths.toListString()}''")
      }
    } catch (NoSuchFieldException ex) {
      logger.warning("The ClassLoader class '${ClassLoader.class}' doesn't have the static field 'usr_paths', i.e. " +
              "the native JOGL libraries can't be loaded automatically. You'll have to specify them manually using the " +
              "'java.library.path' system property.")
    }
  }

  /**
   * <p>Utility method that copies files.</p>
   *
   */
  def private static copy(source, target) throws IOException {
    def input = null
    def output = null
    try {
      input = source.openStream()
      output = new FileOutputStream(target)

      def buffer = new byte[1024];

      // Read chunk of bytes until there is nothing left (i.e. the number of read bytes equals -1)
      def readBytes = input.read(buffer)
      while (readBytes != -1) {
        output.write(buffer, 0, readBytes)

        // Read the next chunk of bytes
        readBytes = input.read(buffer)
      }
    } finally {
      if (input != null) {
        input.close()
      }
      if (output != null) {
        output.close()
      }
    }
  }

}