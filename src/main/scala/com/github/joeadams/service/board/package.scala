package com.github.joeadams.service

import breeze.linalg.Matrix

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
package object board {
  type Board = Map[Coordinate, SquareMarking]

  case class BoardFlip(flip:Matrix[Int],reverseFlip:Matrix[Int]){
    override def toString={
      s"flip=|${flip(0,0)} ${flip(0,1)}|\n||${flip(1,0)} ${flip(1,1)}|\nreverse:|${reverseFlip(0,0)} ${reverseFlip(0,1)}|\n||${reverseFlip(1,0)} ${reverseFlip(1,1)}|"

    }
  }
  case class BoardWithFlips(board: Board, flips: Seq[BoardFlip])

}
