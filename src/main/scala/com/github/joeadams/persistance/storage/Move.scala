package com.github.joeadams.persistance.storage

import com.github.joeadams.Cases
import com.github.joeadams.Cases.X_OR_O

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
case class Move(gameId:Long,moveNumber:Int,playedAs:X_OR_O,boardPosition:Int,moveTaken:Int) {
  lazy val parentGameVal: Game =GamePersistence.allGames.find(game=>game.id==gameId).get
  def parent=parentGameVal

}
