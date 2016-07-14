package com.github.joeadams.game

import java.time.ZonedDateTime

import com.github.joeadams.Cases._
import com.github.joeadams.model.{Coordinate, StatefulSquare}
import com.github.joeadams.state.{AreWePlayingState, SquareState}
import rx.lang.scala.{Observable, Observer, Subject}
import com.github.joeadams.TicTacToe.TTT
import com.github.joeadams.persistance.service.BoardAsNumber

import scala.collection.immutable.IndexedSeq
import scala.collection.mutable
import com.github.joeadams.ui.PopupHelper.popup

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class Game(playerIs: X_OR_O){

  private val id: Long =ZonedDateTime.now().toEpochSecond
  private val computerIs=if (playerIs==X) O else X
  private var notOver=true

  private val initialBoardPosition =TTT.allCoordinates.map(c=>c->blank).toMap
  private val board: mutable.Map[Coordinate, SquareMarking] =mutable.Map() ++ initialBoardPosition

  TTT.allStatefulSquaresMap.values.map(s=>s.state.subject.onNext(blank))
  if(computerIs==X){
    handleComputerMove(Option.empty)
  }
  TTT.clickListener.filter(letPlayerMove(_)).subscribe(onHumanMove(_))

  private def letPlayerMove(coordinate:Coordinate):Boolean= notOver &&(board.get(coordinate).get==blank)

  private def onHumanMove(coordinate:Coordinate)={
    moveHandler(playerIs,coordinate)
  }


  private def afterMove():Boolean={
    val outcome=moveOutcome()
    outcome match {
      case gameOutcome:GameOutcome =>{
        handleFinishedGame(gameOutcome)
        true
      } //If we could cast it up, the game is over
      case _=>false
    }
  }



  private def handleComputerMove(playerMove:Option[Coordinate]):Unit={
    val coordinate=TTT.strategy.move(id,computerIs,board.toMap,playerMove)
    moveHandler(computerIs,coordinate)
  }



  private def moveHandler(p:X_OR_O,coordinate:Coordinate)={
    val oldBoardState =board.toMap
    board.put(coordinate,p)
    TTT.allStatefulSquaresMap.get(coordinate).get.getState.subject.onNext(p)
    val gameOver=afterMove()
    if (!gameOver&&(playerIs==p)){
      handleComputerMove(Some(coordinate))
    }
  }



  private def handleFinishedGame(gameOutcome:GameOutcome):Unit={
    val popupText=gameOutcome match {
      case WonBy(p)=>{
        if (playerIs==p) "You win!"
        else "I win HA HA HA!"
      }
      case _=> "It is a draw!"
    }
    popup(popupText)
    AreWePlayingState.subject.onNext(no)
    notOver=false
    TTT.strategy.processGameOutcome(id,computerIs,gameOutcome,numberOfMoves())
  }

  def handleQuitGame()={
    notOver=false
    TTT.strategy.processGameQuit(id)
  }

  def numberOfMoves():Int= board.values.filter(squareMarking=>squareMarking!=blank).size


  AreWePlayingState.subject.subscribe((areWePlaying: AreWePlaying) => areWePlaying match {
    case no:NO => if(notOver) handleQuitGame()
    case _=> {}
  })



  private def moveOutcome():MoveOutcome={
    if(playerWon(X)){
      return WonBy(X)
    }
    if(playerWon(O)){
      return WonBy(O)
    }
    val isStillGoing=board.values.exists(s=>s==blank)
    if(isStillGoing)
      stillGoing
    else
      draw
  }


  private def playerWon(p:X_OR_O)=Game.allWinningCombos.exists(squares=>playerWonOnASetOfSquares(p,squares))
  private def playerWonOnASetOfSquares(p:X_OR_O,squares:Set[Coordinate]):Boolean=squares.forall(c=>board.get(c).get==p)


}

object Game{
  private def allWinningCombosFactory():Set[Set[Coordinate]]={
    val allRows: IndexedSeq[Set[Coordinate]] =TTT.yRange.map(n=>TTT.allCoordinates.filter(c=>c.y==n))
    val allColumns: IndexedSeq[Set[Coordinate]] =TTT.xRange.map(n=>TTT.allCoordinates.filter(c=>c.x==n))
    val diagonal=Set(Coordinate(1,1),Coordinate(0,0),Coordinate(-1,-1))
    val reverseDigonal=Set(Coordinate(1,-1),Coordinate(0,0),Coordinate(-1,1))
    val answerInWrongType=allRows ++ allColumns :+ diagonal :+ reverseDigonal
    answerInWrongType.toSet
  }

  val allWinningCombos=allWinningCombosFactory


}


