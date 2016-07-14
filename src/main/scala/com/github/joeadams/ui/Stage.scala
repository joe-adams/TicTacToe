package com.github.joeadams.ui

import javafx.event.EventHandler
import javafx.stage.WindowEvent
import javafx.{event, stage}

import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.text.{Font, Text, TextAlignment}



/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object Stage extends PrimaryStage{

  height = 800
  width = 600
  scene = new Scene {
    onShown= new EventHandler[WindowEvent] {
      override def handle(event: WindowEvent): Unit = PopupHelper.popup("Pick X or O to start playing!")
    }

    root = new VBox() {
      children = Seq(
        new Text() {
          text = "TIC TAC TOE!"
          font = new Font(size = 50)
          textAlignment = TextAlignment.Center
          alignmentInParent = Pos.Center
        },
        WannaPlay,
        Board
      )
    }
  }

}
