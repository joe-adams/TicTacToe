package com.github.joeadams.ui

import com.github.joeadams.service.uiprocessing.WannaPlayFacade

import scalafx.scene.control.Button

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait UIOutputForService {
  def boardButtons: Seq[Button]

  def wannaPlay: WannaPlayFacade
}

object UIOutputForService {
  def apply(): UIOutputForService = new UIOutputForService() {
    val boardButtons = BoardUI.buttons
    val wannaPlay = WannaPlayFacade()
  }
}
