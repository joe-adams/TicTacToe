package com.github.joeadams

import com.github.joeadams.service.IntializeService
import com.github.joeadams.ui.Stage

import scalafx.application.JFXApp


object TicTacToe extends JFXApp {
  stage = Stage

  IntializeService.start()

}

