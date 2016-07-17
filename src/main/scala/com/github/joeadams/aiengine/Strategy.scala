package com.github.joeadams.aiengine

import java.util

import com.github.joeadams.Cases
import Cases.SquareMarking
import com.github.joeadams.model.Coordinate
import Cases._
import com.github.joeadams.persistance.service.Board

import scala.util.Random

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait Strategy {
  def move(board:Board):Coordinate

  def processGameOutcome(id:Long,computerIs:X_OR_O,outcome:GameOutcome,numberOfMoves:Int): Unit

  def processGameQuit(id:Long): Unit

  def pickOneRandomly[T](input:Seq[T]): T =
    if (input.size==1){
      input.head
    } else{
      val vector=input.toVector
      val randomNumber=(new Random).nextInt(vector.size)
      vector(randomNumber)
    }
}
