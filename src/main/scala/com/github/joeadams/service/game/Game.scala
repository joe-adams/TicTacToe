package com.github.joeadams.service.game

import java.time.ZonedDateTime

import com.github.joeadams.service._
import com.github.joeadams.service.aiengine.StrategyImpl
import com.github.joeadams.service.board.{Coordinate, ObserveBoardAsCoordinates, UpdateBoard}
import com.github.joeadams.ui.PopupHelper.popup
import rx.lang.scala.Subscription

import scala.collection.immutable.IndexedSeq
import scala.collection.mutable


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class Game(playerIs: X_OR_O) {

  private val id: Long = ZonedDateTime.now().toEpochSecond
  private val computerIs = if (playerIs == X) O else X
  private val computerPlayer = new StrategyImpl(id)
  private val board: mutable.Map[Coordinate, SquareMarking] = mutable.Map() ++ Coordinate.allCoordinates.map(c => c -> blank).toMap
  private val clickSubscription: Subscription = ObserveBoardAsCoordinates.observe.filter(letPlayerMove).subscribe(onHumanMove(_))
  private val areWePlayingSubscription: Subscription = AreWePlayingState.subject.subscribe((areWePlaying: AreWePlaying) => areWePlaying match {
    case no: NO => handleFinishedGame(quit)
    case _ => {}
  })
  private val subscriptions = Seq(clickSubscription, areWePlayingSubscription)
  private def letPlayerMove(coordinate: Coordinate): Boolean = (board.get(coordinate).get == blank)
  private def onHumanMove(coordinate: Coordinate) = moveHandler(playerIs, coordinate)
  private def afterMove(): Boolean = moveOutcome() match {
      case gameOutcome: GameOutcome => {
        handleFinishedGame(gameOutcome)
        true
      } //If we could cast it up, the game is over
      case _ => false
    }

  private def handleComputerMove(playerMove: Option[Coordinate]): Unit = {
    val coordinate = computerPlayer.move(board.toMap)
    moveHandler(computerIs, coordinate)
  }

  private def moveHandler(p: X_OR_O, coordinate: Coordinate) = {
    board.put(coordinate, p)
    UpdateBoard.move(coordinate,p)
    val gameOver = afterMove()
    if (!gameOver && (playerIs == p)) {
      handleComputerMove(Some(coordinate))
    }
  }


  private def handleFinishedGame(gameOutcome: GameOutcome): Unit = {
    subscriptions.foreach(s => s.unsubscribe())
    endPopup(gameOutcome)
    AreWePlayingState.subject.onNext(no)
    computerPlayer.processGameOutcome(id, computerIs, gameOutcome, numberOfMoves())
  }

  def numberOfMoves(): Int = board.values.filter(squareMarking => squareMarking != blank).size

  private def moveOutcome(): MoveOutcome = {
    if (playerWon(X)) {
      return WonBy(X)
    }
    if (playerWon(O)) {
      return WonBy(O)
    }
    val isStillGoing = board.values.exists(s => s == blank)
    if (isStillGoing)
      stillGoing
    else
      draw
  }

  def endPopup(gameOutcome:GameOutcome)={
    val popupText = gameOutcome match {
      case WonBy(p) => {
        if (playerIs == p) "You win!"
        else "I win HA HA HA!"
      }
      case draw:DRAW => "It is a draw!"
      case quit:QUIT => "Are you kidding me?"
    }
    popup(popupText)
  }

  private def playerWon(p: X_OR_O) = Game.allWinningCombos.exists(squares => playerWonOnASetOfSquares(p, squares))
  private def playerWonOnASetOfSquares(p: X_OR_O, squares: Set[Coordinate]): Boolean = squares.forall(c => board.get(c).get == p)

  def init()={
    UpdateBoard.clearBoard()
    if (computerIs == X) {
      handleComputerMove(Option.empty)
    }
  }

  init()
}

object Game {
  private def allWinningCombosFactory(): Set[Set[Coordinate]] = {
    val allRows: IndexedSeq[Set[Coordinate]] = Coordinate.yRange.map(n => Coordinate.allCoordinates.filter(c => c.y == n))
    val allColumns: IndexedSeq[Set[Coordinate]] = Coordinate.xRange.map(n => Coordinate.allCoordinates.filter(c => c.x == n))
    val diagonal = Set(Coordinate(1, 1), Coordinate(0, 0), Coordinate(-1, -1))
    val reverseDigonal = Set(Coordinate(1, -1), Coordinate(0, 0), Coordinate(-1, 1))
    val answerInWrongType = allRows ++ allColumns :+ diagonal :+ reverseDigonal
    answerInWrongType.toSet
  }

  val allWinningCombos = allWinningCombosFactory

}


