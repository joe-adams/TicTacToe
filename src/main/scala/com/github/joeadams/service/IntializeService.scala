package com.github.joeadams.service

import com.github.joeadams.service.board.ObserveBoardAsCoordinates
import com.github.joeadams.service.dao.GameDbTransactions
import com.github.joeadams.service.game.{AreWePlayingState, Game}
import com.github.joeadams.service.uiprocessing.ObserveWannaPlayButtons.WannaPlayEnum
import com.github.joeadams.service.uiprocessing.{ObserveBoard, ObserveWannaPlayButtons, WannaPlayFacade}
import com.github.joeadams.ui.{PopupHelper, Stage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object IntializeService {
  def start() = {
    GameDbTransactions().ensureAllTables()
    Thread.sleep(1000)
    AreWePlayingState.subject.subscribe((areWePlaying: AreWePlaying) => areWePlaying match {
      case no: NO => {
        WannaPlayFacade().gameEndOrInit()
      }
      case HumanIsPlayingAs(p) => {
        WannaPlayFacade().gameStart()
        new Game(p)
      }
    }
    )

    ObserveWannaPlayButtons().observe.subscribe(_ match {
      case WannaPlayEnum.QUIT=>AreWePlayingState.subject.onNext(no)
      case WannaPlayEnum.X=>AreWePlayingState.subject.onNext(HumanIsPlayingAs(X))
      case WannaPlayEnum.O=>AreWePlayingState.subject.onNext(HumanIsPlayingAs(O))
    })

    ObserveBoard.observe()
    ObserveBoardAsCoordinates.observe()

    //Stage.shownFuture.onComplete(_=>{PopupHelper.popup("Pick X or O to start playing!")})
  }

}
