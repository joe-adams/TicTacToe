package com.github.joeadams.model




import com.github.joeadams.Cases._
import com.github.joeadams.state.SquareState
import com.github.joeadams.ui.{Board, ObserveButton}
import rx.lang.scala.Observable

import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.Priority

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
case class StatefulSquareImpl(coordinate: Coordinate, state:SquareState, button:Button) extends StatefulSquare {

  override def getCoordinate=coordinate

  override def getState=state

  override def clickObservable =ObserveButton.observe(button,coordinate)

  state.subject.subscribe((s:SquareMarking)=>{
    s match {
      case X=>Board.xStyle(button)
      case O=>Board.oStyle(button)
      case blank=>button.text=""
    }
  })


}
