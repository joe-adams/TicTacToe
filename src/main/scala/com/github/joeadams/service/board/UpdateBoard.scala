package com.github.joeadams.service.board

import com.github.joeadams.service.{SquareMarking, _}
import com.github.joeadams.ui.Board
import rx.lang.scala.Subject
import rx.lang.scala.subjects.BehaviorSubject

import scalafx.scene.control.Button


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object UpdateBoard {
  private case class Update(coordinate: Coordinate,marking: SquareMarking)

  private val updates: Subject[Update] =BehaviorSubject[Update](Update(Coordinate(0,0),blank))

  def move(coordinate: Coordinate,marking: SquareMarking)=updates.onNext(Update(coordinate,marking))

  def clearBoard()=Coordinate.allCoordinates.map(c=>move(c,blank))

  updates.subscribe(update=>{
    val id=update.coordinate.uniqueId
    val style=update.marking match {
      case blank:BLANK=>Board.blankStyle(_:Button)
      case X=>Board.xStyle(_:Button)
      case O=>Board.oStyle(_:Button)
    }
    Board.updateButton(id,style)
  })


}
