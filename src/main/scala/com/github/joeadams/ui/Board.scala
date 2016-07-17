package com.github.joeadams.ui


import com.github.joeadams._
import com.github.joeadams.service.board.Coordinate

import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority, RowConstraints}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object Board extends GridPane {
  opacity = 1
  val columnConstraint = new ColumnConstraints() {
    percentWidth = (100 / 33.toDouble)
    minWidth = 200
  }
  val rowConstraint = new RowConstraints() {
    percentHeight = (100 / 33.toDouble)
    minHeight = 200

  }
  columnConstraints = List(columnConstraint, columnConstraint, columnConstraint)
  rowConstraints = List(rowConstraint, rowConstraint, rowConstraint)
  gridLinesVisible = true

  val buttons: Seq[Button] = Coordinate.yRange.map(y => Coordinate.xRange.map(x => {
    val row = x - Coordinate.xRange.min
    val column = y - Coordinate.yRange.min
    val button = new Button() {
      margin = Insets(10)
      hgrow = Priority.Always
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      maxWidth = Double.MaxValue
      font = new Font(50)
    }
    Board.add(button, row, column)
    button
  })).flatten

  def xStyle(button: Button) = {
    button.textFill = Color.Red
    button.text = "X"
  }

  def oStyle(button: Button) = {
    button.textFill = Color.Green
    button.text = "O"
  }

  def blankStyle(button: Button) = button.text = ""

  def updateButton(id:Int,buttonStyle:Button=>Unit)={
    val button=buttons(id)
    buttonStyle(button)
  }


}