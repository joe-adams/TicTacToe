package com.github.joeadams.service.uiprocessing

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import scalafx.scene.control.Button



trait ObserveButton {
  def observe[ID](button: Button, id: ID): Observable[ID]
}

object ObserveButton {
  def apply(): ObserveButton = new ObserveButton {
    override def observe[ID](button: Button, id: ID): Observable[ID] = {
      val subject = PublishSubject[ID]()
      val handler = new EventHandler[MouseEvent] {
        def handle(e: MouseEvent): Unit = {
          subject.onNext(id)
        }
      }
      button.addEventHandler(MouseEvent.MOUSE_CLICKED, handler)
      val observable: Observable[ID] = subject
      observable
    }
  }
}


