package com.github.joeadams.persistance.storage

import com.github.joeadams.Cases
import com.github.joeadams.Cases._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
case class Game(id:Long,computerAs:X_OR_O,outcome:GameOutcome,moves:Int) {
  lazy val scoreVal: Int={
    outcome match {
      case outcome:DRAW=>0
      case WonBy(p)=>{
        val absoluteScore=(10-moves)*(10-moves)
        if(computerAs==p){
          absoluteScore
        } else{
          absoluteScore * -1
        }
      }
    }
  }

  def score=scoreVal

}
