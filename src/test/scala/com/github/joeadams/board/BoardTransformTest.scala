package com.github.joeadams.board

import com.github.joeadams.service.board.{Board, BoardTransforms}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class BoardTransformTest  extends FunSuite with BeforeAndAfter with ScalaFutures{

  test("Minimizes Board"){
    val boardTransforms=BoardTransforms()

    val board=Board.fromInt(6723)
    val newBoardNumber=boardTransforms.minimumBoardRepresentations(board)
    assert(newBoardNumber==163)
  }

}
