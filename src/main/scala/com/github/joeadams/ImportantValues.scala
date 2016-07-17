package com.github.joeadams

import com.github.joeadams.aiengine.Strategy
import com.github.joeadams.model.{Coordinate, StatefulSquareImpl}
import rx.lang.scala.Observable

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait ImportantValues {

  def xRange:Range
  def yRange:Range
  def allCoordinates: Set[Coordinate]
  def coordinatesById: Map[Int,Coordinate]
  def allStatefulSquaresMap: Map[Coordinate, StatefulSquareImpl]
  def clickListener: Observable[Coordinate]
  def getOpponent(gameId:Long):Strategy
  def initialize:Unit
  def log(l:String):Unit

}
