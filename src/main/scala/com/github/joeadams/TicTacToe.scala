package com.github.joeadams


import com.github.joeadams.ui.Stage
import rx.lang.scala.{Observable, Observer, Subject, Subscription}

import scalafx.application.JFXApp
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.{Font, Text, TextAlignment}



object TicTacToe  extends JFXApp {
  stage = Stage

  //This is the center of our dependency injection system.
  val TTT:ImportantValues=ImportantValuesImpl
  TTT.initialize
}

