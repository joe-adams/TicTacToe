package com.github.joeadams.service.game

import com.github.joeadams.service._
import rx.lang.scala.Subject
import rx.lang.scala.subjects.BehaviorSubject

object AreWePlayingState {
  private val theSubject: Subject[AreWePlaying] = BehaviorSubject(no)
  def subject = theSubject
}






