package com.github.joeadams.service.aiengine

import com.github.joeadams.service._
import com.github.joeadams.service.board.Board.Board
import com.github.joeadams.service.board.{BoardTransforms, Coordinate}
import com.github.joeadams.service.dao.{DbAction, GameDbService}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait Strategy {
  def move(board: Board): Coordinate

  def processGameOutcome(id: Long, outcome: GameOutcome, numberOfMoves: Int): Unit

}
//extends BoardTransforms with ConvertBoardToAndFromNumber with GameDbService with DbAction.Transactor
object Strategy{


}
