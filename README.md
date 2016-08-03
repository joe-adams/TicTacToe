# TicTacToe

This is a tic tac toe game.  To play it you may simply download the tictactoe.jar and run java -jar tictactoe.jar on the command line.  This assumes you have Java on your machine.  It will create an H2 database which is just a flat file in the directory where the .jar file is.

If you have Scala and sbt you can build the code with “sbt assembly”.  You can also do “sbt clean compile test assembly” if you want to be more through.

The game starts playing very badly, but learns as it goes.  It keeps a database of previous games and avoids making mistakes it made in previous games. The part of the code that selects the moves doesn’t actually understand the rules, it can just look up if moves lead to winning or losing in the past.

However, the game can flip the orientation of the board so you can’t “trick” it by playing a in one corner and then trying it in another.

I use a few different technologies in here.  This is written in Scala.  The UI is written ScalaFX, and messaging is wired up with RxScala.

For memory it uses a simple H2 database.  If you want to give the program amnesia, you may simply delete the database file.  On top of H2, I use Slick.

To allow the program to recognize different orientations as symmetric, I use matrix multiplication. I use the Breeze library for that, however, bringing a library in for the simple matrix multiplication I perform is probably not necessary.  I use the matrices to flip the board, much like one would with the transform on an SVG image.

My basic technique for keeping a compressed memory of previous games in the database is as follows:  I represent any tic tac toe board as a number.  Each square is a number from zero to eight.  And blank, O, and X are 0, 1, and 2.  So for each square, you raise 3 to it’s number (0-8) and then you multiple it by what’s in there (0-2).  So it gets a number.

Okay, but then I have a second way to represent the board!  In my second way, it is a coordinate system.  Each square gets an X and Y coordinate.  The coordinates are -1, 0, and 1.  When I have the board in this representation, I can flip it with the matrices.  My transformation matrices are all 2x2.  Each spot has a number -1, 0, or 1.  I have every permutation, so I have eight of them.  

Okay, so I flip the board 8 different ways.  And for each way I flip the board, I go back to my first representation, and look for the smallest number.  The smallest number is chosen and the orientation that is remembered in the database.  This is an arbitrary choice.  I could choose the biggest, for example.

Because we have a system of efficiently board positions, as we play we can remember our position after each move, and then we can remember if we won or lost a game, and how long a game lasted.  In future games, we can look up if making a move put us in a position we’ve been in before and if we should go back to that position.
