package com.github.joeadams.service.board

import org.scalatest.{FlatSpec, Matchers}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class BoardAsNumberTest   extends FlatSpec with Matchers{
  val boardAsNumber=ConvertBoardToAndFromNumber()

  "Board as Number" should "convert correctly " in{
    val b=
      """
        |bbx
        |bbb
        |bbb
      """.stripMargin

    val board=Util.stringToBoard(b)
    val n=boardAsNumber.boardToNumber(board)
    val b2=boardAsNumber.numberToBoard(n)
    println(b2)
  }
}
