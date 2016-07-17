package com.github.joeadams.persistance.service

import com.github.joeadams.Cases._
import com.github.joeadams.TicTacToe.TTT
import com.github.joeadams.model.Coordinate

import scala.collection.immutable.IndexedSeq

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

/**
  * We implement an arbitary code that gives every board position a unique number
  */
object BoardAsNumber {

  def numberForBoard(map:Map[Coordinate, SquareMarking]):Int={
    val squareValues=map.map({case(coordinate:Coordinate,marking:SquareMarking)=>
      val m=squareMarkingAsInt(marking)
      val u=coordinate.uniqueId
      val squareValue=m * Math.pow(3,u)
      squareValue.toInt
    })
    val sum=squareValues.reduce(_+_)
    sum
  }

  def getSquareFromNumber(id:Int,boardNumber:Int):SquareMarking={
    /*val reduced= if(boardNumber>threePowers(id+1)){
      (boardNumber % threePowers(id + 1))
    } else {
      boardNumber
    }
    val i: Int =reduced%threePowers(id)
    intAsSquareMarking(i)*/
    val reduced:Int=boardNumber/threePowers(id)
    val i=reduced%3
    intAsSquareMarking(i)
  }

  def getBoardFromNumber(boardNumber:Int):Map[Coordinate, SquareMarking]=
    TTT.allCoordinates.map((coordinate: Coordinate)=>{
          val id=coordinate.uniqueId
          val marking=getSquareFromNumber(id,boardNumber)
          coordinate->marking
        })(collection.breakOut)




  def squareMarkingAsInt(squareMarking: SquareMarking)=squareMarking match {
    case s if s==X =>2
    case s if s==O =>1
    case s if s==blank => 0
  }

  def intAsSquareMarking(input: Int)=input match {
    case i if i==2=>X
    case i if i==1 =>O
    case i if i==0 =>blank
  }



  def threePower(i:Int)=Math.pow(3,i).toInt

  val threePowers =(0 to 9).map(i=>{
    val p=threePower(i)
    i->p
  }).toMap


  def extractDigit(input:Int,base:Int)={
    val quotient=input/base
    quotient%3
  }


}
