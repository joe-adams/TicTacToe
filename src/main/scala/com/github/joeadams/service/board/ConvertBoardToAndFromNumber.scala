package com.github.joeadams.service.board

import com.github.joeadams._
import com.github.joeadams.service._


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

/**
  * We implement an arbitary code that gives every board position a unique number
  */

trait ConvertBoardToAndFromNumber {
  def boardToNumber(board:Board): Int

  def numberToBoard(i:Int):Board

  def getSquareFromNumber(id: Int, boardNumber: Int): SquareMarking
}

object ConvertBoardToAndFromNumber {

  def apply(): ConvertBoardToAndFromNumber = ConvertBoardToAndFromNumberImpl
}
