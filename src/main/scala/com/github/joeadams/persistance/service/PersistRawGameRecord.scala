package com.github.joeadams.persistance.service


import com.github.joeadams.persistance.service.RawGameRecords.{RawGameRecord, RawMove}
import com.github.joeadams.persistance.storage.{Game, GamePersistence, Move, MovePersistence}
import com.github.joeadams.TicTacToe.TTT

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object PersistRawGameRecord {
  // case class RawMove(computerIs:X_OR_O,board: Map[Coordinate, SquareMarking],move:Coordinate)
  def persist(gameRecord: RawGameRecord) = {
    var moveNumberVal = 0
    //Note that MoveNumber is more of a primary key, it doesn't matter what order the move was
    def moveNumber() = {
      moveNumberVal += 1
      moveNumberVal
    }

    val newMoves=gameRecord.moveList.map(oldMove=>{
      val newBoard=BoardTransforms.transposeBoard(gameRecord.transform,oldMove.board)
      val newMove=BoardTransforms.transposeCoordinate(oldMove.move,gameRecord.transform)
      RawMove(oldMove.computerIs,newBoard,newMove)
    })
    val moves = newMoves.map(rawMove => {
      val board = BoardAsNumber.numberForBoard(rawMove.board)
      val coordinate = rawMove.move.uniqueId
      Move(gameRecord.id, moveNumber(), gameRecord.computerIs, board, coordinate)
    })
    MovePersistence.addMoves(moves)
    val game = Game(gameRecord.id, gameRecord.computerIs, gameRecord.gameOutcome, gameRecord.numberOfMoves)
    GamePersistence.addGame(game)
  }
}
