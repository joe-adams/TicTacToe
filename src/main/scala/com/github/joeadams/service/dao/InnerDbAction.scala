package com.github.joeadams.service.dao



import com.github.joeadams.service.dao.Tables._
import slick.dbio.Effect._
import slick.dbio.NoStream
import slick.profile._
import com.github.joeadams.service.dao.slickapi._
import slick.jdbc.meta.MTable
import slick.profile.{FixedSqlAction, FixedSqlStreamingAction}



/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait InnerDbAction {
  def getGameMove(boardPosition: Int): FixedSqlStreamingAction[Seq[(Game, Move)], (Game, Move), Read]

  def getLoss(boardPosition: Int): FixedSqlAction[Option[Int], NoStream, Read]

  def getWin(boardPosition: Int): FixedSqlAction[Boolean, NoStream, Read]

  def addMoves(newMoves: Seq[Move]): FixedSqlAction[Option[Int], NoStream, Write]

  def addGame(game: Game): FixedSqlAction[Int, NoStream, Write]

  def addWin(win: Int): FixedSqlAction[Int, NoStream, Write]

  def addLoss(loss: Loss): FixedSqlAction[Int, NoStream, Write]

  def updateLossIfToLowerLevel(loss: Loss): FixedSqlAction[Int, NoStream, Write]

  def getTables(table:TableQuery[_ <: Table[_]]): BasicStreamingAction[Vector[MTable], MTable, Read]

  def tableCreate(table:TableQuery[_ <: Table[_]]): FixedSqlAction[Unit, NoStream, Schema]
}

object InnerDbAction{

  def apply(): InnerDbAction =default


  object default extends InnerDbAction{
    override def getGameMove(boardPosition:Int) = games.join(moves).on(_.id === _.gameId).filter(_._2.newBoardPosition===boardPosition).sortBy(_._1.id.desc).result

    override def getLoss(boardPosition:Int)= {
      println("get loss")
      val l=losses.filter(_.position === boardPosition).map(_.level).min.result
      println("got loss")
      l
    }

    override def getWin(boardPosition:Int) = wins.filter(_.position===boardPosition).max.isDefined.result

    override def addMoves(newMoves:Seq[Move]) =moves ++= newMoves

    override def addGame(game:Game)=games+=game

    override def addWin(win:Int)=wins+=win

    override def addLoss(loss:Loss)={
      println("add loss")
      val a=losses+=loss
      println("added")
      a
    }

    override def updateLossIfToLowerLevel(loss:Loss)={
      println("update")
      val existingRecord =losses.filter(_.position===loss.position).filter(_.level>loss.level).map(_.level)
      println("exist")
      val a=existingRecord.update(loss.level)
      println("updated")
      a
    }

    override def getTables(table:TableQuery[_ <: Table[_]]):BasicStreamingAction[Vector[MTable], MTable, Read]  = MTable.getTables(table.baseTableRow.tableName)

    override def tableCreate(table:TableQuery[_ <: Table[_]])=table.schema.create

  }
}
