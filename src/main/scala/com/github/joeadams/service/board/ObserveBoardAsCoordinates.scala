package com.github.joeadams.service.board

import com.github.joeadams.service.uiprocessing.ObserveBoard

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object ObserveBoardAsCoordinates {

  def observe()={
    ObserveBoard.observe().map(i=>Coordinate.fromId(i))
  }

}
