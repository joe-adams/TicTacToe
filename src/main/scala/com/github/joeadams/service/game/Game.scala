package com.github.joeadams.service.game

import java.time.ZonedDateTime

import com.github.joeadams.service._
import com.github.joeadams.service.aiengine.StrategyImpl
import com.github.joeadams.service.board.Board._
import com.github.joeadams.service.board.{Board, Coordinate, ObserveBoardAsCoordinates, UpdateBoard}
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
  private val computerPlayer = new StrategyImpl(id,computerIs)
  private val board: mutable.Map[Coordinate, SquareMarking] = Board.blankBoard.mutable
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
    computerPlayer.processGameOutcome(id, gameOutcome, numberOfMoves())
  }

  def endPopup(gameOutcome:GameOutcome)={
    val popupText = gameOutcome match {
      case lost:COMPUTER_LOST => "You win!"
      case won: COMPUTER_WON => "I win HA HA HA!"
      case draw:DRAW => "It is a draw!"
      case quit:QUIT => "Are you kidding me?"
    }
    popup(popupText)
  }

  private def playerWon(p: X_OR_O) = Game.playerWon(p,board.toMap)
  private def numberOfMoves()=Game.numberOfMoves(board.toMap)

  private def moveOutcome():MoveOutcome= () match {
    case _ if playerWon(computerIs) =>won
    case _ if playerWon(playerIs) => lost
    case _ if board.values.exists(_ == blank) => stillGoing
    case _ => draw
  }


  def init()={
    UpdateBoard.clearBoard()
    if (computerIs == X) {
      handleComputerMove(Option.empty)
    }
  }

  init()
}

object Game {
  def allWinningCombosFactory(): Set[Set[Coordinate]] = {
    val allRows: IndexedSeq[Set[Coordinate]] = Coordinate.yRange.map(n => Coordinate.allCoordinates.filter(c => c.y == n))
    val allColumns: IndexedSeq[Set[Coordinate]] = Coordinate.xRange.map(n => Coordinate.allCoordinates.filter(c => c.x == n))
    val diagonal = Set(Coordinate(1, 1), Coordinate(0, 0), Coordinate(-1, -1))
    val reverseDigonal = Set(Coordinate(1, -1), Coordinate(0, 0), Coordinate(-1, 1))
    val answerInWrongType = allRows ++ allColumns :+ diagonal :+ reverseDigonal
    answerInWrongType.toSet
  }
  val allWinningCombos = allWinningCombosFactory
  def playerWon(p: X_OR_O, board: Board) = Game.allWinningCombos.exists(squares => playerWonOnASetOfSquares(p, squares,board))
  def playerWonOnASetOfSquares(p: X_OR_O, squares: Set[Coordinate], board: Board): Boolean = squares.forall(board.map(_)==p)
  def numberOfMoves(board: Board): Int = board.s.filter(_!= blank).size

}


