package com.github.joeadams.service.uiprocessing

import com.github.joeadams.ui.BoardUI
import rx.lang.scala.Observable


object ObserveBoard {

  val board = BoardUI

  val buttonRange = 0 to board.buttons.size - 1

  def observe(): Observable[Int] = Observable.from(buttonRange.map(index => {
    val button = board.buttons(index)
    ObserveButton().observe(button, index)
  })).flatten

}
