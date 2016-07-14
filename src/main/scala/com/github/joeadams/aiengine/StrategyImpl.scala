package com.github.joeadams.aiengine

import com.github.joeadams.Cases._
import com.github.joeadams.model.Coordinate
import com.github.joeadams.persistance.service.BoardTransforms._
import com.github.joeadams.persistance.service.{BoardAsNumber, BoardTransforms, PersistRawGameRecord, RawGameRecords}
import com.github.joeadams.persistance.storage.{Game, GamePersistence, Move, MovePersistence}
import com.github.joeadams.TicTacToe.TTT
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Future

/*
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object StrategyImpl  extends Strategy{

  override def move(id:Long,computerIs:X_OR_O, board:Map[Coordinate, SquareMarking],playerMove:Option[Coordinate]):Coordinate={
    val newStrategyForGame=if (strategyForGame.id==id)
      strategyForGame
    else
      new StrategyForGame(id)
    strategyForGame=newStrategyForGame

    strategyForGame.move(computerIs,board,playerMove)
  }

  override def processGameOutcome(id:Long,computerIs:X_OR_O,outcome:GameOutcome,numberOfMoves:Int) =Future{
    strategyForGame.processGameOutcome(id,computerIs,outcome,numberOfMoves)
  }

  override def processGameQuit(id:Long): Unit={}

  var strategyForGame=StrategyForGame(0)






  case class StrategyForGame(id:Long){
    val allTransforms=BoardTransforms.getAllTransforms

    var availableTransforms=BoardTransforms.getAllTransforms

    def setAvailableTransforms(a:Seq[Transform]): Unit ={
      availableTransforms=a
    }

    def processGameOutcome(id:Long,computerIs:X_OR_O,outcome:GameOutcome,numberOfMoves:Int): Unit ={
      val rawrecord=RawGameRecords.processOutcome(id,computerIs,outcome,numberOfMoves,availableTransforms.head)
      PersistRawGameRecord.persist(rawrecord)
    }



    def move(computerIs:X_OR_O,board:Map[Coordinate,SquareMarking],playerMove:Option[Coordinate]):Coordinate={


      val boardAsNumber=BoardAsNumber.numberForBoard(board)
      val move=findMove(computerIs,board,playerMove)
      val moveAsNumber=move.uniqueId

      RawGameRecords.addMove(id,computerIs,board,move)
      move
    }

    def findMove(computerIs:X_OR_O,board:Map[Coordinate,SquareMarking],playerMove:Option[Coordinate]):Coordinate= {
      if (availableTransforms.size > 1 && playerMove.isDefined) {
        val coordinate = playerMove.get
        setAvailableTransforms(BoardTransforms.minimumTransposesForCoordinate(coordinate, availableTransforms))
      }
      val rawAvailableMoves: Set[Coordinate] = board.filter({ case (c: Coordinate, s: SquareMarking) => s == blank }).map(t => t._1).toSet
      if (availableTransforms.size == 1) {
        lookupBasedOnOneTranspose(rawAvailableMoves, board, availableTransforms.head)
      } else {
        val myMove = lookupBasedOnOneManyTransposes(rawAvailableMoves, board, availableTransforms)
        setAvailableTransforms(BoardTransforms.minimumTransposesForCoordinate(myMove, availableTransforms))
        myMove
      }
    }

    def lookupBasedOnOneTranspose(unTransposedMoves:Set[Coordinate],unTransposedBoard:Map[Coordinate, SquareMarking],transpose:Transform):Coordinate={
      val board=BoardTransforms.transposeBoard(transpose,unTransposedBoard)
      val moves=unTransposedMoves.map(transposeCoordinate(_,transpose))
      val boardAsNumber=BoardAsNumber.numberForBoard(board)
      val movesAsNumbers=moves.map(_.uniqueId)
      val lookedUp=lookupFromHistory(boardAsNumber,movesAsNumbers)
      val chosenMove=pickOneRandomly(lookedUp.toSeq)
      val toCoordinates=TTT.coordinatesById(chosenMove)
      BoardTransforms.transposeCoordinate(toCoordinates,transpose)
    }

    def lookupBasedOnOneManyTransposes(unTransposedMoves:Set[Coordinate],unTransposedBoard:Map[Coordinate, SquareMarking],transposes:Seq[Transform]):Coordinate={
      val transposeWeAreUsing=pickOneRandomly(transposes.toSeq)
      val transposedMoves=unTransposedMoves.map(transposeCoordinate(_,transposeWeAreUsing))
      val filteredMoves =filterMovesBaseOnSymmetry(transposedMoves,transposes)

      val board=BoardTransforms.transposeBoard(transposeWeAreUsing,unTransposedBoard)
      val boardAsNumber=BoardAsNumber.numberForBoard(board)
      val movesAsNumbers=filteredMoves.map(_.uniqueId)
      val lookedUp: Set[Int] =lookupFromHistory(boardAsNumber,movesAsNumbers)
      val chosenMove=pickOneRandomly(lookedUp.toSeq)
      val toCoordinates=TTT.coordinatesById(chosenMove)
      BoardTransforms.transposeCoordinate(toCoordinates,transposeWeAreUsing)
    }

    def lookupFromHistory(boardAsANumber:Int,movesAsNumbers:Set[Int]):Set[Int]={
      if (movesAsNumbers.size==0) throw new IllegalStateException
      val previousMoves: Map[Int, Seq[Move]] =MovePersistence.getMoves().filter(move=>move.boardPosition==boardAsANumber).groupBy(move=>move.moveTaken)
      if (previousMoves.size==0){
        return movesAsNumbers
      }
      val previousWinScores:Map[Int,Double]=previousMoves.mapValues(turnMovesSequenceIntoScoreAverage)
      val movesNeverTried: Map[Int, Double] =movesAsNumbers.filter(m=> !previousMoves.contains(m)).map(moveNumber=>moveNumber->0.toDouble).toMap
      val allMoves: Map[Int, Double] =(previousWinScores ++ movesNeverTried).filterKeys(movesAsNumbers)
      val bestMoveRating=allMoves.values.max
      val bestMovesById=allMoves.filter({case(move:Int,rating:Double)=>rating==bestMoveRating}).keySet
      bestMovesById
    }

    def filterMovesBaseOnSymmetry(moves:Set[Coordinate],transposes:Seq[Transform])=moves.filter(move=>{
        val min=BoardTransforms.minimallyTransposedCoordinate(move,transposes)
        (move==min||(!moves.contains(min)))
      })


    def turnMovesSequenceIntoScoreAverage(moves:Seq[Move])={
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

  }

}
