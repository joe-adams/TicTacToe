package com.github.joeadams.ui

import com.github.joeadams.service.uiprocessing.WannaPlayFacade

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.HBox

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */


object WannaPlay extends HBox with WannaPlayFacade {
  alignmentInParent = Pos.Center
  children = Seq()
  override val xButton = new Button("I wanna be X!")
  override val oButton = new Button(("I wanna be O!"))
  override val quit = new Button("I quit!")
  val displayedDuringPlay = Seq(quit)
  val displayedBetweenGames = Seq(xButton, oButton)

  override def gameStart(): Unit = children = displayedDuringPlay

  override def gameEndOrInit(): Unit = children = displayedBetweenGames
}




