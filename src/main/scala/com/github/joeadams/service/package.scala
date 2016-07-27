package com.github.joeadams

import java.io.{File, FileWriter}
import scala.io.Source


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
package object service {

  trait SquareMarking{
    def asInt:Int
  }

  sealed case class BLANK() extends SquareMarking {
    override val toString = "b"
    override val asInt=0
  }

  val blank = BLANK()

  trait X_OR_O extends SquareMarking

  sealed case class O_PLAYER() extends X_OR_O{
    override def toString = "O"
    override val asInt=1
  }

  val O =O_PLAYER()

  sealed case class X_PLAYER() extends X_OR_O{
    override def toString = "X"
    override val asInt=2
  }

  val X = X_PLAYER()

  trait AreWePlaying

  sealed case class NO() extends AreWePlaying {
    override def toString = "Are We Playing? No"
  }

  val no = NO()

  case class HumanIsPlayingAs(p: X_OR_O) extends AreWePlaying

  trait MoveOutcome

  sealed case class STILL_GOING() extends MoveOutcome {
    override def toString = "Game still Going!"
  }

  val stillGoing = STILL_GOING()

  trait GameOutcome extends MoveOutcome

  sealed case class DRAW() extends GameOutcome {
    override def toString = "d"
  }

  val draw = DRAW()

  sealed case class QUIT() extends GameOutcome {
    override def toString = "q"
  }

  val quit = QUIT()

  sealed case class COMPUTER_WON() extends GameOutcome {
    override def toString = "w"
  }

  val won=COMPUTER_WON()

  sealed case class COMPUTER_LOST() extends GameOutcome {
    override def toString = "l"
  }

  val lost=COMPUTER_LOST()

  def stringToGameOutcome(s:String)=s match {
    case "w"=>won
    case "l"=>lost
    case "q"=>quit
    case _=>draw
  }

  /**
    *
    * Okay, not too great.  But here's what we have.  We have X and O are are X_OR_O.
    * The SquareMarkings are BLANK, along with X and O.
    * AreWePlaying: NO, and a Case Class of HumanIsPlayingAs which says if the human is x or o
    * The game outcome is a case class that says who won, or it's a Draw.
    * The move outcome is the game outcome, or "still going" meaning that game is still on.
    *
    */

  def log(l: String) = append("log.txt", l)

  def append(filename: String, line: String): Unit = {
    val write = new FileWriter(filename, true)
    try {
      write.append(line + "\n")
    } finally {
      write.close()
    }
  }

  def readFile[O](file: File, lineReader: String => O) = {
    val source = Source.fromFile(file)
    source.getLines().toSeq.map(lineReader)
  }


}
