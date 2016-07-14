package com.github.joeadams.state

import com.github.joeadams.Cases
import Cases._
import rx.lang.scala.Subject
import rx.lang.scala.subjects.BehaviorSubject

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class SquareState {
  private val theSubject:Subject[SquareMarking]=BehaviorSubject(blank)
  def subject=theSubject



}
