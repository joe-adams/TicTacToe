package com.github.joeadams.service.dao


import com.github.joeadams.service.dao.slickapi._
import slick.profile.{FixedSqlAction, FixedSqlStreamingAction}
import com.github.joeadams.service.dao.Tables._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait DbAction {
  def getGameMove(boardPosition: Int): Future[Seq[(Game, Move)]]

  def addLoss(loss: Loss): Future[Int]

  def updateLossIfToLowerLevel(loss: Loss): Future[Int]

  def addGame(game: Game): Future[Int]

  def addWin(win: Int): Future[Int]

  def getLoss(boardPosition: Int): Future[Option[Int]]

  def getWin(boardPosition: Int): Future[Boolean]

  def addMoves(newMoves: Seq[Move]): Future[Option[Int]]

  def getTables(table: TableQuery[_<:Table[_]]): Future[Vector[MTable]]

  def tableCreate(table:TableQuery[_<:Table[_]]): Future[Unit]

}



trait DbActionWithComponents extends DbAction{

  def db:Database
  def inner:InnerDbAction

  override def getGameMove(boardPosition: Int): Future[Seq[(Game, Move)]] = db.run(inner.getGameMove(boardPosition))

  override def addLoss(loss: Loss): Future[Int] = db.run(inner.addLoss(loss))

  override def updateLossIfToLowerLevel(loss: Loss): Future[Int] = db.run(inner.updateLossIfToLowerLevel(loss))

  override def addGame(game: Game): Future[Int] = db.run(inner.addGame(game))

  override def addWin(win: Int): Future[Int] = db.run(inner.addWin(win))

   override def getLoss(boardPosition: Int): Future[Option[Int]] = {
    val l=db.run(inner.getLoss(boardPosition)).map(_.headOption)
     l.onComplete(f=>println((s"on complete: $f")))
    println(s"loss is $l")
    l
  }

  override def getWin(boardPosition: Int): Future[Boolean] = db.run(inner.getWin(boardPosition))

  override def addMoves(newMoves: Seq[Move]): Future[Option[Int]] = db.run(inner.addMoves(newMoves))

  override def getTables(table: TableQuery[_<:Table[_]]): Future[Vector[MTable]] ={

    db.run(inner.getTables(table))
  }

  override def tableCreate(table:TableQuery[_<:Table[_]]): Future[Unit] =db.run(table.schema.create)
}




