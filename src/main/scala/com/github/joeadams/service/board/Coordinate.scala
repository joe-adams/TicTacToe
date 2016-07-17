package com.github.joeadams.service.board

import breeze.linalg.Matrix

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

case class Coordinate(x: Int, y: Int) extends Ordered[Coordinate] {

  val uniqueId = ((x + 1) * 3) + (y + 1)

  override def equals(that: Any): Boolean =
    that match {
      case that: Coordinate => that.uniqueId == this.uniqueId
      case _ => false
    }

  //The fact that a board position can have a unique number, and also ordering, is useful.
  override def hashCode: Int = uniqueId

  override def compare(that: Coordinate): Int = Ordering.by((c: Coordinate) => c.uniqueId).compare(this, that)

  override def toString = s"(x: $x y: $y) id:${uniqueId}"

  def asMatrix=Matrix.create(1, 2, Array(this.x, this.y))
}

object Coordinate{
  val yRange = (-1 to 1)
  val xRange = (-1 to 1)
  val allCoordinates = (-1 to 1).map(x => (-1 to 1).map(y => Coordinate(x, y))).flatten.toSet
  val fromId: Map[Int, Coordinate] = allCoordinates.map(c => c.uniqueId -> c).toMap
  def fromMatrix(m:Matrix[Int])=Coordinate(m(0,0),m(0,1))
}



