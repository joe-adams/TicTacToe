package com.github.joeadams.service.uiprocessing

import com.github.joeadams.ui.WannaPlay

import scalafx.scene.control.Button

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait WannaPlayFacade {

  def xButton: Button
  def oButton: Button
  def quit: Button
  def gameStart(): Unit
  def gameEndOrInit(): Unit

}

object WannaPlayFacade {
  def apply(): WannaPlayFacade = WannaPlay
}
