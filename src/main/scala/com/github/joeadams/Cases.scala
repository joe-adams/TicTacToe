package com.github.joeadams

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object Cases {

  trait SquareMarking

  sealed case class BLANK() extends SquareMarking{
    override def toString="X"
  }

  val blank=BLANK()

  trait X_OR_O extends SquareMarking

  val X=new Object() with X_OR_O{
    override def toString="X"
  }
  val O=new Object() with X_OR_O{
    override def toString="X"
  }

  trait AreWePlaying

  sealed case class NO() extends AreWePlaying{
    override def toString="Are We Playing? No"
  }
  val no=NO()

  case class HumanIsPlayingAs(p:X_OR_O) extends AreWePlaying{
    override def toString=s"Human is playins as ${p}"
  }

  trait MoveOutcome

  sealed case class STILL_GOING() extends MoveOutcome{
    override def toString="game still going"
  }

  val stillGoing=STILL_GOING()

  trait GameOutcome extends MoveOutcome

  sealed case class DRAW() extends GameOutcome{
    override def toString="draw"
  }

  val draw=DRAW()

  case class WonBy(p:X_OR_O) extends GameOutcome{
    override def toString=s"won by $p"
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



}
