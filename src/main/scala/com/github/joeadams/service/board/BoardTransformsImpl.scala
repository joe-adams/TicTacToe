package com.github.joeadams.service.board

import breeze.linalg.Matrix
import com.github.joeadams.service.SquareMarking

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class BoardTransformsImpl(convertBoardToAndFromNumber:ConvertBoardToAndFromNumber=ConvertBoardToAndFromNumber()) extends BoardTransforms{

  def boardToNumber(board:Board)=convertBoardToAndFromNumber.boardToNumber(board)
  def numberToBoard(i:Int)=convertBoardToAndFromNumber.numberToBoard(i)



  override val getAllBoardFlips: Seq[BoardFlip] ={
    val flipX = Matrix((-1, 0), (0, 1))
    val flipY = Matrix((1, 0), (0, -1))
    val mirrorFlip = Matrix((0, 1), (1, 0))
    val xFlips = Seq(identity, flipX)
    val xyFlips = xFlips ++ xFlips.map(f => f * flipY)
    val allFlips = xyFlips ++ xyFlips.map(f => f * mirrorFlip)
    val allTransforms=allFlips.map(matrix=>{
      val inverse=allFlips.find(i=>matrix*i==identity).get
      BoardFlip(matrix,inverse)
    })
    allTransforms
  }

  override def flipCoordinate(coordinate: Coordinate,boardFlip: BoardFlip):Coordinate={
    val transformedMatrix=coordinate.asMatrix*boardFlip.flip
    Coordinate.fromMatrix(transformedMatrix)
  }

  override def reverseFlipCoordinate(coordinate: Coordinate,boardFlip: BoardFlip):Coordinate={
    val transformedMatrix=coordinate.asMatrix*boardFlip.reverseFlip
    Coordinate.fromMatrix(transformedMatrix)
  }

  private def reverseTransformCoordinate(coordinate: Coordinate,boardFlip: BoardFlip)={
    val transformedMatrix=coordinate.asMatrix*boardFlip.reverseFlip
    Coordinate.fromMatrix(transformedMatrix)
  }

  override def flipBoard(flip: BoardFlip, board: Board): Board =
    board.map({ case (coordinate: Coordinate, squareMarketing: SquareMarking) =>
      val transformedCoordinate = flipCoordinate(coordinate, flip)
      (transformedCoordinate -> squareMarketing)
    })

  override def reverseflipBoard(flip: BoardFlip, board: Board): Board =
    board.map({ case (coordinate: Coordinate, squareMarketing: SquareMarking) =>
      val transformedCoordinate = reverseFlipCoordinate(coordinate, flip)
      (transformedCoordinate -> squareMarketing)
    })

  override def minimumBoardRepresentations(board: Board): BoardWithFlips = {
    val minimumBoardValue: Int = getAllBoardFlips.map(flipBoard(_, board)).map(boardToNumber).min
    val minimumBoard: Board = numberToBoard(minimumBoardValue)
    val flips = getAllBoardFlips.filter((boardFlip:BoardFlip)=>{
      val transposedBoard:Board = flipBoard(boardFlip, board)
      val number:Int = boardToNumber(transposedBoard)
      number == minimumBoardValue
    })
    BoardWithFlips(minimumBoard, flips)
  }


}
