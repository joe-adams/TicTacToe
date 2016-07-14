package com.github.joeadams.game

import com.github.joeadams.{Cases, TicTacToe}
import TicTacToe._
import Cases.{AreWePlaying, HumanIsPlayingAs, NO}
import com.github.joeadams.state.AreWePlayingState

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object GameSetter {

  def startObserving():Unit={
      AreWePlayingState.subject.subscribe((areWePlaying: AreWePlaying) => areWePlaying match {
        case areWePlaying:NO => {}
        case HumanIsPlayingAs(p) => new Game(p)
      })
  }


}
