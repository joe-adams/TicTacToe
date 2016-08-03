package com.github.joeadams.ui

import com.github.joeadams.service.uiprocessing.WannaPlayFacade

import scalafx.scene.control.Button


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
