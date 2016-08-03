# TicTacToe

This is a tic tac toe game.  To play it you may simply download the tictactoe.jar and run java -jar tictactoe.jar on the command line.  This assumes you have Java on your machine.  It will create an H2 database which is just a flat file in the directory where the .jar file is.

If you have Scala and sbt you can build the code with “sbt assembly”.  You can also do “sbt clean compile test assembly” if you want to be more through.

The game starts playing very badly, but learns as it goes.  It keeps a database of previous games and avoids making mistakes it made in previous games. It has a "strategy" algorthim for playing the game.  The strategy algorthim doesn't actually understand the rules. It just learns by rote.

However, the game does flip the orientation of the board, so if a game is a mirror image of a previous game it will apply what it learned.  (It will apply any symmetry, not just a mirror image.  It might capitalize on symmetries of the board a bit better than you do.)

I use a few different technologies in here.  This is written in Scala.  The UI is written ScalaFX, and messaging is wired up with RxScala.

For memory it uses a simple H2 database.  If you want to give the program amnesia, you may simply delete the database file.  On top of H2, I use Slick.

To allow the program to recognize different orientations as symmetric, I use matrix multiplication. I use the Breeze library for that, however, bringing a library in for the simple matrix multiplication I perform is probably not necessary.  I use the matrices to flip the board, much like one would with the transform on an SVG image.

My basic technique for keeping a compressed memory of previous games in the database is as follows:  I represent any tic tac toe board as a number.  Each square is assigned a number from zero to eight.  And the things that can be on a square a blank, O, and X are 0, 1, and 2.  And then for each square, you raise 3 to it’s number (0-8).  (So  in a sense, every square gets some number between 1 and 6561).  And then you multiply that number by what’s in the square (0-2).  So every board is a number between 0 and 13,122.  Some of these numbers don't appear in legal games, but that's okay.

Okay, but then I have a second way to represent the board!  In my second way, it is a coordinate system.  Each square gets an X and Y coordinate.  The coordinates are -1, 0, and 1.  When I have the board in this representation, I can flip it with the matrices.  My transformation matrices are all 2x2.  Each spot has a number -1, 0, or 1.  I take four basic matrices, an identity matrix, a horizontal flip, a vertical flip, and a diagonal flip. I do every permutation of multiplying one to all for matrices together for eight matrices.

SO, I flip the board 8 different ways.  And for each way I flip the board, I go back to my FIRST representation (a number between 0 and 13,122), and look for the smallest number.  The smallest number is chosen and the orientation that is remembered in the database.  This is an arbitrary choice.  I could have chosen to use the biggest number instead and it would have worked out the same.

Because we have a system of efficiently storing board positions, as we play we can remember our position after each move, and then we can remember if we won or lost a game, and how long a game lasted.  In future games, we can look up if making a move put us in a position we’ve been in before and if we should go back to that position.
