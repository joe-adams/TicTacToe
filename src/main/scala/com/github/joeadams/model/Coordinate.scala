package com.github.joeadams.model

import com.github.joeadams.TicTacToe.TTT

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

  case class Coordinate(x:Int,y:Int) extends Ordered[Coordinate]{

    val uniqueId=((x+1)*3)+(y+1)
    override def equals(that: Any): Boolean =
      that match {
        case that:Coordinate=>that.uniqueId==this.uniqueId
        case _ => false
      }
    //The fact that a board position can have a unique number, and also ordering, is useful.
    override def hashCode:Int=uniqueId

    override def compare(that: Coordinate): Int = Ordering.by((c:Coordinate)=>c.uniqueId).compare(this,that)

    override def toString= s"(x: $x y: $y) id:${uniqueId}"
  }

