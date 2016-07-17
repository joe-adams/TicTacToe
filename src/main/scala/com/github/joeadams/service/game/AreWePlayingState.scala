package com.github.joeadams.service.game

import com.github.joeadams.service._
import rx.lang.scala.Subject
import rx.lang.scala.subjects.BehaviorSubject


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

object AreWePlayingState {
  private val theSubject: Subject[AreWePlaying] = BehaviorSubject(no)

  def subject = theSubject
}






