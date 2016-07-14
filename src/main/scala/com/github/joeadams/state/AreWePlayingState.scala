package com.github.joeadams.state

import com.github.joeadams.Cases
import Cases.AreWePlaying
import rx.lang.scala.Subject
import rx.lang.scala.subjects.{BehaviorSubject, PublishSubject}


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

object AreWePlayingState{
  private val theSubject:Subject[AreWePlaying]=BehaviorSubject(Cases.no)
  def subject=theSubject
}






