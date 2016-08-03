package com.github.joeadams.service.board

import breeze.linalg.Matrix

import scala.collection.immutable.ListMap

case class Coordinate(x: Int, y: Int) extends Ordered[Coordinate] {

  val id = ((y + 1) * 3) + (x + 1)

  override def equals(that: Any): Boolean =
    that match {
      case that: Coordinate => that.id == this.id
      case _ => false
    }

  //The fact that a board position can have a unique number, and also ordering, is useful.
  override def hashCode: Int = id

  override def compare(that: Coordinate): Int = Ordering.by((c: Coordinate) => c.id).compare(this, that)

  override def toString = s"(x: $x y: $y) id:${id}"

  def asMatrix=Matrix.create(1, 2, Array(this.x, this.y))
}

object Coordinate{
  val yRange = (-1 to 1)
  val xRange = (-1 to 1)
  val allCoordinates = yRange.map(y => xRange.map(x => Coordinate(x, y))).flatten.toSet
  private val allIdTupleList=allCoordinates.map(c => (c.id,c)).toSeq.sortBy(_._1)
  val id: Map[Int, Coordinate] = ListMap(allIdTupleList:_*)
  def fromMatrix(m:Matrix[Int])=Coordinate(m(0,0),m(0,1))

  implicit def tup2Coordinate(t:Tuple2[Int,Int])=Coordinate(t._1,t._2)
  implicit def int2Coordinate(id:Int)=Coordinate.id(id)
}



