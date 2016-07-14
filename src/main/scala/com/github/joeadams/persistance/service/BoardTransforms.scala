package com.github.joeadams.persistance.service

import breeze.linalg.Matrix
import com.github.joeadams.Cases.SquareMarking
import com.github.joeadams.model.Coordinate
import com.github.joeadams.persistance.service.RawGameRecords.RawMove
import com.github.joeadams.TicTacToe._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object BoardTransforms {

  //We can flip the board different ways and have the equivalent board.
  //We can do translations like they do with in SVG

  type Transform=Matrix[Int]

  private def getAllTransformsInit():Seq[Transform]={
    val identity =  Matrix((1,0),(0,1))
    val flipX=      Matrix((-1,0),(0,1))
    val flipY=      Matrix((1,0), (0,-1))
    val mirrorFlip= Matrix((0,1),(1,0))
    val xFlips =Seq(identity,flipX)
    val xyFlips =xFlips ++  xFlips.map(f=>f*flipY)
    val allFlips = xyFlips ++ xyFlips.map(f=>f*mirrorFlip)
    allFlips
  }

  private val allTransformsVal=getAllTransformsInit()

  def getAllTransforms=allTransformsVal

  def transposeCoordinate(coordinate:Coordinate,t:Transform):Coordinate={
      val cAsMatrix=Matrix.create(1,2,Array(coordinate.x,coordinate.y))
      val transposed=cAsMatrix*t
      Coordinate(transposed(0,0),transposed(0,1))
  }

  def transposeBoard(t:Transform,board:Map[Coordinate, SquareMarking]):Map[Coordinate, SquareMarking]=
    board.map({case (coordinate:Coordinate,squareMarketing:SquareMarking)=>
        val tranposed=transposeCoordinate(coordinate,t)
      (tranposed->squareMarketing)
    })

  /**
    *  Our standard of minimum is arbitary, but it is consitent, that's all that matters.
    *
    * @param c
    * @param transforms
    * @return
    */
  def minimallyTransposedCoordinate(c:Coordinate, transforms:Seq[Transform])=transforms.map(t=>{transposeCoordinate(c,t)}).min


  def minimumTransposesForCoordinate(c:Coordinate,transforms:Seq[Transform])={
    //Coordinate has an id and implements comparable
    val minCoordinate = minimallyTransposedCoordinate(c,transforms)
    transforms.filter(t=>transposeCoordinate(c,t)==minCoordinate)
  }

  def transposeMoves(rawMoves:List[RawMove]):List[RawMove]={
    val coordinates=rawMoves.map(r=>r.move)
    val transform=getTransformFinal(coordinates)
    rawMoves.map(oldMove=>{
      val newBoard=BoardTransforms.transposeBoard(transform,oldMove.board)
      val newMove=BoardTransforms.transposeCoordinate(oldMove.move,transform)
      RawMove(oldMove.computerIs,newBoard,newMove)
    })
  }

  def getTransformFinal(coordinates:Seq[Coordinate]):Transform={
    var transforms=BoardTransforms.getAllTransforms
    var counter=0
    while (transforms.size>1&&counter<coordinates.size){
      val coordinate=coordinates(counter)
      transforms=BoardTransforms.minimumTransposesForCoordinate(coordinate,transforms)
      counter=counter+1
    }
    transforms.head
  }



  def getTransforms(coordinates:Seq[Coordinate]):Seq[Transform]={
    var transforms=BoardTransforms.getAllTransforms
    var counter=0
    while (transforms.size>1&&counter<coordinates.size){
      val coordinate=coordinates(counter)
      transforms=BoardTransforms.minimumTransposesForCoordinate(coordinate,transforms)
      counter=counter+1
    }
    transforms
  }

}
