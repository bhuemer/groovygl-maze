== Howto use / start the application:
- For your convenience, I've chosen to provide an Ant build file so that
it's rather easy to build this project on any machine. Just enter the
project's home directory on your disk in a terminal and type one of the
following commands:
 - 'ant package' - Recreates the .JAR file of the project (coursework.jar)
 - 'ant run' - Configures the classpath and runs the previously built .JAR file
 - 'ant package run' - Both of the previous steps in one go

Once you've started the application a JFrame will appear, similar to the
GraphicsWindow applications that we've written so far. At first the user
will be positioned at the entry of the maze (which is by default always
at the top right corner of the maze - the exit is always at the bottom
left corner btw.). You can start walking around in the maze using the
arrow keys of your keyboard, <Up> will move the player forward, <Down>
backward respectively, <Left> makes the player gradually turn left,
<Right> - surprise, surprise - makes the player turn right. If you want
to generate the maze, i.e. reinitialize the application, just press the
<R> button (for reinitialize).

In order to get an overview of where you are in the maze, hit the <Space>
key to switch to the bird's perspective mode. You'll see the maze from
above and a red triangle that indicates both the player's position and 
orientation within the maze.

== Notice:
- In order to implement this application I have used a Java OpenGL binding
library called, JOGL. Note that there is even a Java Specification Request
for that particular binding library available, it's called JSR-231, see
also: https://jogl.dev.java.net/. However, this binding library uses JNI
in order to access some native libraries that it provides as well.
However, usually you don't have to care about those libraries as this
application comes with those libraries already and it knows how to load
them automatically, but due to those libraries I cannot tell whether
this application works on a Mac OS X machine as I do not have access to
such a machine in order to test it. 

== Known issues:
- Sometimes if you switch to the bird's perspective (using the space key),
you might notice that there are some walls missing in the maze. Before you
assume that the maze isn't generated correctly, take a look at that
particular wall in the first person's perspective and you'll see that in
fact the wall is there! It's just missing in the bird's perspective as
that's what's called a clipping error. The problem is that the camera is
right above the particular wall that is missing, so under those
circumstances it's perfectly valid to assume that the user doesn't see
the wall (walls are really thin after all). I would have to implement a
custom clipping strategy to get rid of that error, which I think is
outside of the scope of this coursework.

- Missing collision detection: The user is able to walk through walls,
the program doesn't check that. Again, I would have to implement a
collision detection algorithm which also usually requires you to build
something like a tree of all the shapes that are around you, etc..
which is I think again out of the scope of this coursework.

== References:
In order to implement this application I have tutorials about OpenGL,
most notably I have read the famous "NeHe tutorials". In particular,
lesson #8 [2] of those tutorials explains how my camera implementation
works. Additionally I've partly read the book "Pro JavaTM 6 3D Game
Development: Java 3DTM, JOGL, JInput, and JOAL APIs, 2007, by Andrew
Davison, published by Appress".

The maze generation algorithm has been implemented according to a
description of it on Wikipedia [4].

[1]: http://nehe.gamedev.net/
[2]: http://nehe.gamedev.net/data/articles/article.asp?article=08
[3]: http://jerome.jouvie.free.fr/OpenGl/index.php
[4]: http://en.wikipedia.org/wiki/Maze_generation_algorithm#Depth-first_search