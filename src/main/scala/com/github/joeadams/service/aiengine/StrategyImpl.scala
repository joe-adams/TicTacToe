package com.github.joeadams.service.aiengine



import com.github.joeadams.service._
import com.github.joeadams.service.board._
import com.github.joeadams.service.persistance.{Game, GamePersistence, Move, MovePersistence}

import scala.collection.mutable.ListBuffer

/*
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
case class StrategyImpl(gameId: Long,
                        boardTransforms: BoardTransforms=BoardTransforms(),
                        convertBoardToAndFromNumber: ConvertBoardToAndFromNumber=ConvertBoardToAndFromNumber()) extends Strategy {

  private val moveList = ListBuffer.empty[Move]


  case class BestMovesOnBoard(bestMoveNumbers: Set[Int], boardNumber: Int)

  case class MoveResult(actualMove: Coordinate, recordedMove: Move)

  def processGameOutcome(id: Long, computerIs: X_OR_O, outcome: GameOutcome, numberOfMoves: Int): Unit = {
    if (quit==outcome){
      return
    }
    val game = Game(gameId, computerIs, outcome, numberOfMoves)
    GamePersistence.addGame(game)
    MovePersistence.addMoves(moveList)
  }


  def move(board: Board): Coordinate = {
    val moveResult = findMove(board)
    moveList += moveResult.recordedMove
    moveResult.actualMove
  }

  private def findMove(board: Board): MoveResult = {
    val rawAvailableMoves: Set[Coordinate] = board.filter({ case (c: Coordinate, s: SquareMarking) => s == blank }).map(t => t._1).toSet
    val boardWithFlips = boardTransforms.minimumBoardRepresentations(board)
    if (boardWithFlips.flips.size == 1) {
      lookupBasedOnOneFlip(rawAvailableMoves, board, boardWithFlips.flips.head)
    } else {
      lookupBasedOnOneManyFlips(rawAvailableMoves, board, boardWithFlips.flips)
    }
  }

  private def lookupBasedOnOneFlip(unFlippedMoves: Set[Coordinate], unflippedBoard: Map[Coordinate, SquareMarking], boardFlip: BoardFlip): MoveResult = {
    val board = boardTransforms.flipBoard(boardFlip, unflippedBoard)
    val moves = unFlippedMoves.map(boardTransforms.flipCoordinate(_, boardFlip))
    val boardAsNumber = convertBoardToAndFromNumber.boardToNumber(board)
    val movesAsNumbers = moves.map(_.uniqueId)
    val chosenMove: Int = lookupFromHistory(boardAsNumber, movesAsNumbers)
    val toCoordinatesButStillTransposed = Coordinate.fromId(chosenMove)
    val actualMove = boardTransforms.reverseFlipCoordinate(toCoordinatesButStillTransposed,boardFlip)
    val recoredMove = Move(gameId, boardAsNumber, chosenMove)
    MoveResult(actualMove, recoredMove)
  }

  private def lookupBasedOnOneManyFlips(unFlippedMoves: Set[Coordinate], unflippedBoard: Board, boardFlips: Seq[BoardFlip]): MoveResult = {
    val flipWeAreUsing = pickOneRandomly(boardFlips)
    val flippedMoves=unFlippedMoves.map(boardTransforms.flipCoordinate(_, flipWeAreUsing))
    val filterdMoves=filterMovesBaseOnSymmetry(flippedMoves,flipWeAreUsing,(boardFlips.toSet-flipWeAreUsing))
    val boardAsNumber=convertBoardToAndFromNumber.boardToNumber(boardTransforms.flipBoard(flipWeAreUsing, unflippedBoard))
    val chosenMove=lookupFromHistory(boardAsNumber,filterdMoves.map(_.uniqueId))
    val toCoordinatesButStillTransposed = Coordinate.fromId(chosenMove)
    val actualMove = boardTransforms.reverseFlipCoordinate(toCoordinatesButStillTransposed,flipWeAreUsing)
    val recoredMove = Move(gameId, boardAsNumber, chosenMove)
    MoveResult(actualMove, recoredMove)
  }

  private def lookupFromHistory(boardAsANumber: Int, movesAsNumbers: Set[Int]): Int = {
    if (movesAsNumbers.size == 0) throw new IllegalStateException
    val moveList =movesAsNumbers.map(move=>HistoricalMove.makeFromHistory(move,boardAsANumber)).toList
    val bestMove: HistoricalMove =moveList.max
    bestMove.moveAsInt
  }

  private def filterMovesBaseOnSymmetry(moves: Set[Coordinate], flipWeAreUsing:BoardFlip,flipsWeArentUsing:Set[BoardFlip]): Set[Coordinate] =
    moves.filter((move: Coordinate) => {
      val alternateCoordinates=flipsWeArentUsing.map((flip)=>boardTransforms.flipCoordinate(move,flip)).filter((coordinate)=>moves.contains(coordinate))
      val smaller=alternateCoordinates.find(coordinate=>coordinate.uniqueId<move.uniqueId)
      val f=smaller.isDefined
      !f
  })


  private def turnMovesSequenceIntoScoreAverage(moves: Seq[Move]) = {
    val intSeq = moves.flatMap(getGameScoreFromMove(_))
    if (intSeq.isEmpty) {
      0.toDouble
    } else {
      val size = intSeq.size
      val sum = intSeq.reduce(_ + _).toDouble
      val average = sum / size
      average
    }
  }


  private def getGameScoreFromMove(move: Move): Option[Int] = {
    val gameOpt: Option[Game] = GamePersistence.getGames().find(_.id == move.gameId)
    gameOpt.map(game => game.scoreVal)
  }

}

