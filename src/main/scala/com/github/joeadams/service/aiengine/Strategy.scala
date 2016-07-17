package com.github.joeadams.service.aiengine


import com.github.joeadams.service._
import com.github.joeadams.service.board.{Board, Coordinate}

import scala.util.Random

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait Strategy {
  def move(board: Board): Coordinate

  def processGameOutcome(id: Long, computerIs: X_OR_O, outcome: GameOutcome, numberOfMoves: Int): Unit


  def pickOneRandomly[T](input: Seq[T]): T =
    if (input.size == 1) {
      input.head
    } else {
      val vector = input.toVector
      val randomNumber = (new Random).nextInt(vector.size)
      vector(randomNumber)
    }
}
