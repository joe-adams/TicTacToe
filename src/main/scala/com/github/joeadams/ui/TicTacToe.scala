package com.github.joeadams.ui

import com.github.joeadams.service.IntializeService

import scalafx.application.JFXApp


object TicTacToe extends JFXApp {
  stage = Stage

  IntializeService.start()

}

