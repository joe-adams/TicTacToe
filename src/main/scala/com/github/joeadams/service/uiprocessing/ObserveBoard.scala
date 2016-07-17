package com.github.joeadams.service.uiprocessing

import com.github.joeadams.ui.Board
import rx.lang.scala.Observable

import scala.collection.immutable.IndexedSeq

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object ObserveBoard {

  val board = Board

  val buttonRange = 0 to board.buttons.size - 1

  def observe(): Observable[Int] = Observable.from(buttonRange.map(index => {
    val button = board.buttons(index)
    ObserveButton().observe(button, index)
  })).flatten

}
