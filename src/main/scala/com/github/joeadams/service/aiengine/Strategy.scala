package com.github.joeadams.service.aiengine

import com.github.joeadams.service._
import com.github.joeadams.service.board.Board.Board
import com.github.joeadams.service.board.Coordinate


trait Strategy {
  def move(board: Board): Coordinate

  def processGameOutcome(id: Long, outcome: GameOutcome, numberOfMoves: Int): Unit

}
//extends BoardTransforms with ConvertBoardToAndFromNumber with GameDbService with DbAction.Transactor
object Strategy{


}
