package com.github.joeadams.service.board

import breeze.linalg.Matrix

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait BoardTransforms {
  val identity = Matrix((1, 0), (0, 1))

  def flipCoordinate(coordinate: Coordinate,transform: BoardFlip):Coordinate

  def reverseFlipCoordinate(coordinate: Coordinate,transform: BoardFlip):Coordinate

  def getAllBoardFlips:Seq[BoardFlip]

  def flipBoard(flip:BoardFlip,board:Board):Board

  def reverseflipBoard(flip:BoardFlip,board:Board):Board

  def minimumBoardRepresentations(board: Board): BoardWithFlips

  def minimumCoordinateForMove(coordinate: Coordinate,flips:Seq[BoardFlip]):Coordinate

}

object BoardTransforms{
  def apply(): BoardTransforms = new BoardTransformsImpl(ConvertBoardToAndFromNumberImpl)
}


