package com.github.joeadams.service.aiengine



import com.github.joeadams.service._
import com.github.joeadams.service.board.Board._
import com.github.joeadams.service.board._
import com.github.joeadams.service.dao.MoveHistory.{HasLossRank, MoveRank}
import com.github.joeadams.service.dao.Tables._
import com.github.joeadams.service.dao.{GameDbTransactions, Transactor}

import scala.collection.IterableView
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Random
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

/*
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
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
    val moves: Set[Coordinate] = board.kv.filter(_.s==blank).map(_.c).toSet
    val minBoardsWithMoves: Map[Int, Set[Coordinate]] =moves.groupBy(move=>minBoardAfterMove(board,move))
    val boards =minBoardsWithMoves.keySet.toSeq
    println(s"boards $boards")
    val boardsToRanks: Map[Int, MoveRank] = boards.map(boardAndRank).toMap
    println(s"boardsToRank $boardsToRanks")

    //if (moveList.size>0) handlePossibleNeedToAddLossRank(boardsToRanks.values.toSeq,moveList.last.newBoardPosition)


    println("handled loss rank")
    val boardPicked: Int =boardsToRanks.maxBy(_._2)._1
    println(s"picked: $boardPicked")
    val setOfMoves: Set[Coordinate] =minBoardsWithMoves(boardPicked)
    println(s"set of moves: $setOfMoves")
    val moveCoordinate: Coordinate =pickOneRandomly(setOfMoves.toSeq)
    val moveNumber=10-moves.size
    val moveRecord=Move(gameId,moveNumber,boardPicked)
    MoveResult(moveCoordinate,moveRecord)
  }

  def boardAndRank(board:Int):(Int,MoveRank)={
    val b=board
    println(s"boardAndRank $b")
    val r: MoveRank =rankMove(b)
    println(s"leaving boardAndRank $r")
    (b,r)
  }

  private def rankMove(board:Int)= Await.result(gameDbTransactionSupplier().checkMove(board),Duration.Inf).createMoveRank()
  private def minBoardAfterMove(board:Board,move:Coordinate): Int = boardTransforms.minimumBoardRepresentations(board + (move->computerIs))

  private def handlePossibleNeedToAddLossRank(moveRanks: Seq[MoveRank],lastBoard:Int)={
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

  private def optionalLossRankForMove(moves:Seq[MoveRank]):Option[Int]={
    val lossRanksOpt=moves.map(lossRank)
    if (lossRanksOpt.exists(_==None)) None
    else{
      val highestLossRank=lossRanksOpt.map(_.get).map(_.lossRank).max
      Some(highestLossRank+1)
    }
  }


  private def pickOneRandomly[T](input: Seq[T]): T =
    if (input.size == 1) {
      input.head
    } else {
      val vector = input.toVector
      val randomNumber = (new Random).nextInt(vector.size)
      vector(randomNumber)
    }

}

