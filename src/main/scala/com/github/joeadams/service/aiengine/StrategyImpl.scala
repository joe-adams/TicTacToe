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
      lookupBasedOnOneTranspose(rawAvailableMoves, board, boardWithFlips.flips.head)
    } else {
      lookupBasedOnOneManyTransposes(rawAvailableMoves, board, boardWithFlips.flips)
    }
  }

  private def lookupBasedOnOneTranspose(unTransposedMoves: Set[Coordinate], unTransposedBoard: Map[Coordinate, SquareMarking], boardFlip: BoardFlip): MoveResult = {
    val board = boardTransforms.flipBoard(boardFlip, unTransposedBoard)
    val moves = unTransposedMoves.map(boardTransforms.flipCoordinate(_, boardFlip))
    val boardAsNumber = convertBoardToAndFromNumber.boardToNumber(board)
    val movesAsNumbers = moves.map(_.uniqueId)
    val lookedUp = lookupFromHistory(boardAsNumber, movesAsNumbers)
    val chosenMoveAsNumber = pickOneRandomly(lookedUp.bestMoveNumbers.toSeq)
    val toCoordinatesButStillTransposed = Coordinate.fromId(chosenMoveAsNumber)
    val actualMove = boardTransforms.reverseFlipCoordinate(toCoordinatesButStillTransposed,boardFlip)
    val recoredMove = Move(gameId, boardAsNumber, chosenMoveAsNumber)
    MoveResult(actualMove, recoredMove)
  }

  private def lookupBasedOnOneManyTransposes(unFlippedMoves: Set[Coordinate], unflippedBoard: Board, boardFlips: Seq[BoardFlip]): MoveResult = {
    val flipWeAreUsing = pickOneRandomly(boardFlips)
    val flippedMoves = unFlippedMoves.map(boardTransforms.flipCoordinate(_, flipWeAreUsing))
    val filteredMoves = filterMovesBaseOnSymmetry(flippedMoves, boardFlips)

    val board = boardTransforms.flipBoard(flipWeAreUsing, unflippedBoard)
    val boardAsNumber = convertBoardToAndFromNumber.boardToNumber(board)
    val movesAsNumbers = filteredMoves.map(_.uniqueId)
    log(s"To look up move.  Board as number: $boardAsNumber")
    log(s"To look up move.  Moves as numbers: $movesAsNumbers")
    log(s"Transposes: $boardFlips")
    log(s"transposedMoves: $flippedMoves")
    val lookedUp: BestMovesOnBoard = lookupFromHistory(boardAsNumber, movesAsNumbers)
    val chosenMoveAsNumber = pickOneRandomly(lookedUp.bestMoveNumbers.toSeq)
    val toCoordinatesButStillTransposed = Coordinate.fromId(chosenMoveAsNumber)
    val actualMove = boardTransforms.reverseFlipCoordinate(toCoordinatesButStillTransposed, flipWeAreUsing)
    val recoredMove = Move(gameId, boardAsNumber, chosenMoveAsNumber)
    MoveResult(actualMove, recoredMove)
  }

  private def lookupFromHistory(boardAsANumber: Int, movesAsNumbers: Set[Int]): BestMovesOnBoard = {
    if (movesAsNumbers.size == 0) throw new IllegalStateException
    val previousMoves: Map[Int, Seq[Move]] = MovePersistence.getMoves().filter(move => move.boardPosition == boardAsANumber).groupBy(move => move.moveTaken)
    if (previousMoves.size == 0) {
      return BestMovesOnBoard(movesAsNumbers, boardAsANumber)
    }
    val previousWinScores: Map[Int, Double] = previousMoves.mapValues(turnMovesSequenceIntoScoreAverage)
    val movesNeverTried: Map[Int, Double] = movesAsNumbers.filter(m => !previousMoves.contains(m)).map(moveNumber => moveNumber -> 0.toDouble).toMap
    val allMoves: Map[Int, Double] = (previousWinScores ++ movesNeverTried).filterKeys(movesAsNumbers)
    val bestMoveRating = allMoves.values.max
    val bestMovesById: Set[Int] = allMoves.filter({ case (move: Int, rating: Double) => rating == bestMoveRating }).keySet
    BestMovesOnBoard(bestMovesById, boardAsANumber)

  }

  private def filterMovesBaseOnSymmetry(moves: Set[Coordinate], boardFlips:  Seq[BoardFlip]) = moves.filter(move => {
    val min = boardTransforms.minimumCoordinateForMove(move,boardFlips)
    (move == min || (!moves.contains(min)))
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
    val gameOpt: Option[Game] = GamePersistence.allGames().find(_.id == move.gameId)
    gameOpt.map(game => game.scoreVal)
  }

}

