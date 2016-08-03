package com.github.joeadams.ui

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.Popup



object PopupHelper {
  def popup(popupText: String) = {
    val p = new Popup {

      inner =>
      autoHide = true
      content.add(new StackPane {
        children = List(
          new Rectangle {
            width = 300
            height = 200
            arcWidth = 20
            arcHeight = 20
            fill = Color.LightBlue
            stroke = Color.Gray
            strokeWidth = 2
          },
          new BorderPane {
            center = new Label {
              text = popupText
              wrapText = true
              maxWidth = 280
              maxHeight = 140
            }
            bottom = new Button("OK") {
              onAction = { e: ActionEvent => inner.hide() }
              alignmentInParent = Pos.Center
              margin = Insets(10, 0, 10, 0)
            }
          }
        )
      }.delegate
      )
    }
    p.show(Stage)

  }

}

