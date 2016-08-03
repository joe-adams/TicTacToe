package com.github.joeadams.service.board

import breeze.linalg.Matrix
import com.github.joeadams.service.board.Board._

trait BoardTransforms {
  type BoardFlip=Matrix[Int]

  def flipCoordinate(coordinate: Coordinate,boardFlip: BoardFlip):Coordinate

  def flipBoard(board: Board,boardFlip: BoardFlip): Board

  def minimumBoardRepresentations(board: Board): Int

}

object BoardTransforms{

  trait Component{
    def boardTransforms
  }

  def apply(): BoardTransforms = Impl

  object Impl extends BoardTransforms{
    private val getAllBoardFlips: Seq[BoardFlip] ={
      val identity = Matrix((1, 0), (0, 1))
      val flipX = Matrix((-1, 0), (0, 1))
      val flipY = Matrix((1, 0), (0, -1))
      val mirrorFlip = Matrix((0, 1), (1, 0))
      val xFlips = Seq(identity, flipX)
      val xyFlips = xFlips ++ xFlips.map(f => f * flipY)
      val allFlips = xyFlips ++ xyFlips.map(f => f * mirrorFlip)
      allFlips
    }

    override def flipCoordinate(coordinate: Coordinate,boardFlip: BoardFlip):Coordinate={
      val transformedMatrix=coordinate.asMatrix*boardFlip
      Coordinate.fromMatrix(transformedMatrix)
    }

    override def flipBoard(board: Board,boardFlip: BoardFlip): Board =
      board.kv.map((kv) =>{
        val transformedCoordinate = flipCoordinate(kv.c, boardFlip)
        (transformedCoordinate -> kv.s)
      }).toMap


    override def minimumBoardRepresentations(board: Board): Int = getAllBoardFlips.map(flipBoard(board,_).toInt).min
  }

}


