package com.github.joeadams.model

import com.github.joeadams.state.SquareState
import rx.lang.scala.Observable

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait StatefulSquare {

  def getCoordinate:Coordinate

  def getState:SquareState

  def clickObservable: Observable[Coordinate]

}
