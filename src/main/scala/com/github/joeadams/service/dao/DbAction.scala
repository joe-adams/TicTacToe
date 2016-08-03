package com.github.joeadams.service.dao

import com.github.joeadams.service.dao.Tables._
import com.github.joeadams.service.dao.slickapi._
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  override def getGameMove(boardPosition: Int)= db.run(inner.getGameMove(boardPosition))

  override def addLoss(loss: Loss): Future[Int] = db.run(inner.addLoss(loss))

  override def updateLossIfToLowerLevel(loss: Loss) = db.run(inner.updateLossIfToLowerLevel(loss))

  override def addGame(game: Game) = db.run(inner.addGame(game))

  override def addWin(win: Int) = db.run(inner.addWin(win))

   override def getLoss(boardPosition: Int)= db.run(inner.getLoss(boardPosition)).map(_.headOption)

  override def getWin(boardPosition: Int)= db.run(inner.getWin(boardPosition))

  override def addMoves(newMoves: Seq[Move]) = db.run(inner.addMoves(newMoves))

  override def getTables(table: TableQuery[_<:Table[_]]) = db.run(inner.getTables(table))

  override def tableCreate(table:TableQuery[_<:Table[_]])=db.run(table.schema.create)
}




