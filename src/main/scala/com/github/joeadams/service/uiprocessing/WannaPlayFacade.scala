package com.github.joeadams.service.uiprocessing

import com.github.joeadams.ui.WannaPlay

import scalafx.scene.control.Button


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
