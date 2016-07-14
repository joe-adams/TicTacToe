package com.github.joeadams.ui

import com.github.joeadams.Cases

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.HBox
import com.github.joeadams.state.AreWePlayingState
import rx.lang.scala.{Observable, Subject}
import Cases._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object WannaPlay extends HBox {
  alignmentInParent = Pos.Center
  children = Seq()

  val xButton = new Button("I wanna be X!")
  val oButton = new Button(("I wanna be O!"))
  val quit = new Button("I quit!")

  val displayedDuringPlay = Seq(quit)
  val displayedBetweenGames = Seq(xButton, oButton)


  val xObserve = ObserveButton.observe(xButton, HumanIsPlayingAs(X))
  val oObserve = ObserveButton.observe(oButton, HumanIsPlayingAs(O))
  val noObserve = ObserveButton.observe(quit, no)
  val observers = Seq(xObserve, oObserve, noObserve)

  val observeAll = Observable.from(observers).flatten
  observeAll.subscribe(AreWePlayingState.subject)



  AreWePlayingState.subject.subscribe((areWePlaying: AreWePlaying) => areWePlaying match {
    case no:NO => WannaPlay.children = displayedBetweenGames
    case HumanIsPlayingAs(p)  => WannaPlay.children = displayedDuringPlay
  })
}

