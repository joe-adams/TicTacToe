package com.github.joeadams.service.board

import com.github.joeadams._
import com.github.joeadams.service._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object ConvertBoardToAndFromNumberImpl extends ConvertBoardToAndFromNumber{


  override def boardToNumber(board:Board): Int = {
    val squareValues = board.map({ case (coordinate: Coordinate, marking: SquareMarking) =>
      val m = squareMarkingAsInt(marking)
      val u = coordinate.uniqueId
      val squareValue = m * Math.pow(3, u)
      squareValue.toInt
    })
    val sum = squareValues.reduce(_ + _)
    sum
  }

  override def getSquareFromNumber(id: Int, boardNumber: Int): SquareMarking = {
    val reduced: Int = boardNumber / threePowers(id)
    val i = reduced % 3
    intAsSquareMarking(i)
  }

  def numberToBoard(boardNumber: Int): Board =
    Coordinate.fromId.keySet.map(id=>{
      val marking=getSquareFromNumber(id,boardNumber)
      val coordinate=Coordinate.fromId(id)
      coordinate->marking
    }).toMap


  def squareMarkingAsInt(squareMarking: SquareMarking) = squareMarking match {
    case s if s == X => 2
    case s if s == O => 1
    case s if s == blank => 0
  }

  def intAsSquareMarking(input: Int) = input match {
    case i if i == 2 => X
    case i if i == 1 => O
    case i if i == 0 => blank
  }


  def threePower(i: Int) = Math.pow(3, i).toInt

  val threePowers = (0 to 9).map(i => {
    val p = threePower(i)
    i -> p
  }).toMap


  def extractDigit(input: Int, base: Int) = {
    val quotient = input / base
    quotient % 3
  }


}
