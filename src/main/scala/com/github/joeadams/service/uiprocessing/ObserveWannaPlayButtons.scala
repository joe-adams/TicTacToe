package com.github.joeadams.service.uiprocessing

import com.github.joeadams.service.uiprocessing.ObserveWannaPlayButtons.WannaPlayEnum
import com.github.joeadams.ui.WannaPlay
import rx.lang.scala.Observable


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait ObserveWannaPlayButtons {

  def observe: Observable[WannaPlayEnum.Value]
}

object ObserveWannaPlayButtons {

  object WannaPlayEnum extends Enumeration {
    val X, O, QUIT = Value
  }

  private val xOb = ObserveButton().observe(WannaPlay.xButton, WannaPlayEnum.X)
  private val oOb = ObserveButton().observe(WannaPlay.oButton, WannaPlayEnum.O)
  private val qOb = ObserveButton().observe(WannaPlay.quit, WannaPlayEnum.QUIT)
  private val all = Seq(xOb, oOb, qOb)
  private val observeVal = Observable.from(all).flatten

  def apply(): ObserveWannaPlayButtons = new ObserveWannaPlayButtons() {
    def observe = observeVal
  }
}
