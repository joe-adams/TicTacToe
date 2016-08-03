package com.github.joeadams.service.board

import com.github.joeadams.service.uiprocessing.ObserveBoard

object ObserveBoardAsCoordinates {

  def observe()={
    ObserveBoard.observe().map(i=>Coordinate.id(i))
  }

}
