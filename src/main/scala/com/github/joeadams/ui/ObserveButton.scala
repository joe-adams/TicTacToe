package com.github.joeadams.ui

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import scalafx.scene.control.Button

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object ObserveButton {

  def observe[ID](button:Button,id:ID):Observable[ID]={
    val subject = PublishSubject[ID]()
    val handler = new EventHandler[MouseEvent] {
      def handle(e: MouseEvent): Unit = {
        subject.onNext(id)
      }
    }
    button.addEventHandler(MouseEvent.MOUSE_CLICKED, handler)
    val observable:Observable[ID]=subject
    observable
  }

}
