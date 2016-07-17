package com.github.joeadams.aiengine

import com.github.joeadams.Cases._
import com.github.joeadams.model.Coordinate
import com.github.joeadams.persistance.service.BoardTransforms._
import com.github.joeadams.persistance.service._
import com.github.joeadams.persistance.storage.{Game, GamePersistence, Move, MovePersistence}
import com.github.joeadams.TicTacToe.TTT

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
  case class StrategyImpl(gameId:Long) extends Strategy{

  private val moveList= ListBuffer.empty[Move]


    case class BestMovesOnBoard(bestMoveNumbers:Set[Int],boardNumber:Int)
    case class MoveResult(actualMove:Coordinate,recordedMove:Move)

    def processGameOutcome(id:Long,computerIs:X_OR_O,outcome:GameOutcome,numberOfMoves:Int): Unit ={
      val game=Game(gameId,computerIs,outcome,numberOfMoves)
      GamePersistence.addGame(game)
      MovePersistence.addMoves(moveList)

    }



    def move(board:Board):Coordinate={
      val moveResult=findMove(board)
      moveList+=moveResult.recordedMove
      moveResult.actualMove
    }

    private def findMove(board:Board):MoveResult= {
      val rawAvailableMoves: Set[Coordinate] = board.filter({ case (c: Coordinate, s: SquareMarking) => s == blank }).map(t => t._1).toSet
      val boardTransforms=minimumBoardRepresentations(board)
      if (boardTransforms.transforms.size==1){
        lookupBasedOnOneTranspose(rawAvailableMoves,board,boardTransforms.transforms.head)
      } else {
        lookupBasedOnOneManyTransposes(rawAvailableMoves, board, boardTransforms.transforms)
      }
    }

    private def lookupBasedOnOneTranspose(unTransposedMoves:Set[Coordinate],unTransposedBoard:Map[Coordinate, SquareMarking],transpose:Transform):MoveResult={
      val board=BoardTransforms.transposeBoard(transpose,unTransposedBoard)
      val moves=unTransposedMoves.map(transposeCoordinate(_,transpose))
      val boardAsNumber=BoardAsNumber.numberForBoard(board)
      val movesAsNumbers=moves.map(_.uniqueId)
      val lookedUp=lookupFromHistory(boardAsNumber,movesAsNumbers)
      val chosenMoveAsNumber=pickOneRandomly(lookedUp.bestMoveNumbers.toSeq)
      val toCoordinatesButStillTransposed=TTT.coordinatesById(chosenMoveAsNumber)
      val actualMove=BoardTransforms.reverseTransform(toCoordinatesButStillTransposed,transpose)
      val recoredMove=Move(gameId,boardAsNumber,chosenMoveAsNumber)
      MoveResult(actualMove,recoredMove)
    }

    private def lookupBasedOnOneManyTransposes(unTransposedMoves:Set[Coordinate],unTransposedBoard:Map[Coordinate, SquareMarking],transposes:Seq[Transform]):MoveResult={
      val transposeWeAreUsing=pickOneRandomly(transposes)
      val transposedMoves=unTransposedMoves.map(transposeCoordinate(_,transposeWeAreUsing))
      val filteredMoves =filterMovesBaseOnSymmetry(transposedMoves,transposes)

      val board=BoardTransforms.transposeBoard(transposeWeAreUsing,unTransposedBoard)
      val boardAsNumber=BoardAsNumber.numberForBoard(board)
      val movesAsNumbers=filteredMoves.map(_.uniqueId)
      TTT.log(s"To look up move.  Board as number: $boardAsNumber")
      TTT.log(s"To look up move.  Moves as numbers: $movesAsNumbers")
      TTT.log(s"Transposes: $transposes")
      TTT.log(s"transposedMoves: $transposedMoves")
      val lookedUp: BestMovesOnBoard =lookupFromHistory(boardAsNumber,movesAsNumbers)
      val chosenMoveAsNumber=pickOneRandomly(lookedUp.bestMoveNumbers.toSeq)
      val toCoordinatesButStillTransposed=TTT.coordinatesById(chosenMoveAsNumber)
      val actualMove=BoardTransforms.reverseTransform(toCoordinatesButStillTransposed,transposeWeAreUsing)
      val recoredMove=Move(gameId,boardAsNumber,chosenMoveAsNumber)
      MoveResult(actualMove,recoredMove)
    }

    private def lookupFromHistory(boardAsANumber:Int,movesAsNumbers:Set[Int]):BestMovesOnBoard={
      if (movesAsNumbers.size==0) throw new IllegalStateException
      val previousMoves: Map[Int, Seq[Move]] =MovePersistence.getMoves().filter(move=>move.boardPosition==boardAsANumber).groupBy(move=>move.moveTaken)
      if (previousMoves.size==0){
        return BestMovesOnBoard(movesAsNumbers,boardAsANumber)
      }
      val previousWinScores:Map[Int,Double]=previousMoves.mapValues(turnMovesSequenceIntoScoreAverage)
      val movesNeverTried: Map[Int, Double] =movesAsNumbers.filter(m=> !previousMoves.contains(m)).map(moveNumber=>moveNumber->0.toDouble).toMap
      val allMoves: Map[Int, Double] =(previousWinScores ++ movesNeverTried).filterKeys(movesAsNumbers)
      val bestMoveRating=allMoves.values.max
      val bestMovesById: Set[Int] =allMoves.filter({case(move:Int,rating:Double)=>rating==bestMoveRating}).keySet
      BestMovesOnBoard(bestMovesById,boardAsANumber)

    }

    private def filterMovesBaseOnSymmetry(moves:Set[Coordinate],transposes:Seq[Transform])=moves.filter(move=>{
        val min=BoardTransforms.minimallyTransposedCoordinate(move,transposes)
        (move==min||(!moves.contains(min)))
      })


    private def turnMovesSequenceIntoScoreAverage(moves:Seq[Move])={
      val intSeq =moves.flatMap(getGameScoreFromMove(_))
      if (intSeq.isEmpty){
        0.toDouble
      } else {
        val size=intSeq.size
        val sum=intSeq.reduce(_+_).toDouble
        val average=sum/size
        average
      }
    }


    private def getGameScoreFromMove(move:Move):Option[Int]={
      val gameOpt: Option[Game] =GamePersistence.allGames().find(_.id==move.gameId)
      gameOpt.map(game=>game.scoreVal)
    }

  override def processGameQuit(id: Long): Unit = {}
}

