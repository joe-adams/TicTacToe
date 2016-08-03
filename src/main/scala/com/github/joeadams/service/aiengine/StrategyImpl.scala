package com.github.joeadams.service.aiengine

import com.github.joeadams.service._
import com.github.joeadams.service.board.Board._
import com.github.joeadams.service.board._
import com.github.joeadams.service.dao.GameDbTransactions
import com.github.joeadams.service.dao.MoveHistory.{HasLossRank, MoveRank}
import com.github.joeadams.service.dao.Tables._

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import scala.util.Random

case class StrategyImpl(gameId: Long, computerIs: X_OR_O,
                        boardTransforms: BoardTransforms=BoardTransforms(),
                        gameDbTransactionSupplier: ()=>GameDbTransactions=GameDbTransactions.apply) extends Strategy{



  private val moveList = ListBuffer.empty[Move]
  case class BestMovesOnBoard(bestMoveNumbers: Set[Int], boardNumber: Int)

  case class MoveResult(actualMove: Coordinate, recordedMove: Move)

  def processGameOutcome(id: Long, outcome: GameOutcome, numberOfMoves: Int): Unit = {
    if (quit==outcome){
      return
    }
    val f=gameDbTransactionSupplier().processGameAtEnd(gameId,outcome,numberOfMoves,moveList)

  }


  def move(board: Board): Coordinate = {
    val moveResult = findMove(board)
    moveList += moveResult.recordedMove
    moveResult.actualMove
  }

  private def findMove(board: Board): MoveResult = {
    val moves= board.kv.filter(_.s==blank).map(_.c).toSet
    val minBoardsWithMoves =moves.groupBy(move=>minBoardAfterMove(board,move))
    val boards =minBoardsWithMoves.keySet
    val boardsToRanks = boards.map(boardAndRank).toMap
    if (moveList.size>0) handlePossibleNeedToAddLossRank(boardsToRanks.values.toSet,moveList.last.newBoardPosition)
    val boardPicked: Int =boardsToRanks.maxBy(_._2)._1
    val setOfMoves: Set[Coordinate] =minBoardsWithMoves(boardPicked)
    val moveCoordinate: Coordinate =pickOneRandomly(setOfMoves.toVector)
    val moveNumber=10-moves.size
    val moveRecord=Move(gameId,moveNumber,boardPicked)
    MoveResult(moveCoordinate,moveRecord)
  }

  private def boardAndRank(board:Int)= (board,rankMove(board))
  private def rankMove(board:Int)= gameDbTransactionSupplier().checkMove(board).createMoveRank()
  private def minBoardAfterMove(board:Board,move:Coordinate): Int = boardTransforms.minimumBoardRepresentations(board + (move->computerIs))

  private def handlePossibleNeedToAddLossRank(moveRanks: Set[MoveRank],lastBoard:Int)={
    val lossRankOpt=optionalLossRankForMove(moveRanks)
    lossRankOpt match {
      case Some(rank) =>addLossRank(Loss(lastBoard,rank))
      case None=>()
    }
  }

  private def lossRank(moveRank:MoveRank):Option[HasLossRank]=moveRank match{
    case HasLossRank(a,b)=>Some(HasLossRank(a,b))
    case _=>None
  }

  private def addLossRank(loss:Loss):Unit=gameDbTransactionSupplier().registerLosingPathMove(loss)

  private def optionalLossRankForMove(moves:Set[MoveRank]):Option[Int]={
    val lossRanksOpt=moves.map(lossRank)
    if (lossRanksOpt.exists(_==None)) None
    else{
      val highestLossRank=lossRanksOpt.map(_.get).map(_.lossRank).max
      Some(highestLossRank+1)
    }
  }


  private def pickOneRandomly[T](input: Vector[T]): T =
    if (input.size == 1) {
      input.head
    } else {
      val randomNumber = (new Random).nextInt(input.size)
      input(randomNumber)
    }

}

