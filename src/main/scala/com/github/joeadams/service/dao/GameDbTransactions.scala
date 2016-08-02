package com.github.joeadams.service.dao

import com.github.joeadams.service.GameOutcome
import com.github.joeadams.service.dao.Tables.{Loss, Move}

import scala.concurrent.Future

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait GameDbTransactions {
  def processGameAtEnd(gameId:Long, gameOutcome:GameOutcome, numberOfMoves:Int, moves:Seq[Move]):Any
  def checkMove(boardPosition:Int): MoveHistory
  def registerLosingPathMove(loss:Loss): Int
  def ensureAllTables():Any
}



object GameDbTransactions{
  trait GameWithComponents  extends  GameDbService.Default with GameDbTransactions{
    this:Transactor=>
  }


  def apply(): GameDbTransactions = new DefaultWithTransactor

  abstract class DefaultClass extends GameWithComponents with Transactor{
    override def processGameAtEnd(gameId: Long, gameOutcome: GameOutcome, numberOfMoves: Int, moves: Seq[Move]) =
      transaction(processGameAtEnd(gameId,gameOutcome,numberOfMoves,moves,_))

    override def checkMove(boardPosition: Int)= transaction(checkMove(boardPosition,_))

    override def registerLosingPathMove(loss:Loss) = transaction(registerLosingPathMove(loss,_))

    override def ensureAllTables()=transaction(ensureAllTables(_))
  }

  class DefaultWithTransactor extends DefaultClass with Transactor.Default{}

}