package com.github.joeadams.service

import breeze.linalg.Matrix

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
package object board {
  type Board = Map[Coordinate, SquareMarking]

  case class BoardFlip(flip:Matrix[Int],reverseFlip:Matrix[Int])
  case class BoardWithFlips(board: Board, flips: Seq[BoardFlip])

}
