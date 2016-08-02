package com.github.joeadams.dao


import com.github.joeadams.service.dao.{GameDbTransactions, MoveHistory}
import com.github.joeadams.service.dao.MoveHistory.{HasLossRank, MoveRank, NeverTried}
import com.github.joeadams.service.dao.Tables.{Loss, Move}
import com.github.joeadams.service._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import com.github.joeadams.service.dao.slickapi._
import slick.jdbc.meta._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */



class GameDBTransactionsTest   extends FunSuite with BeforeAndAfterAll{
  def gameDbTransactions=new TestGameDbTransactions

  override def beforeAll={
      DropTables.drop()
      Thread.sleep(5000)
      Await.result(new TestGameDbTransactions().ensureAllTables(),Duration.Inf)
      Thread.sleep(5000)
  }

  test("ensure tables"){
    Await.result(new TestGameDbTransactions().ensureAllTables(),Duration.Inf)
    Thread.sleep(5000)
  }


  test("Get positions on no positions"){
    val moveRank =Await.result(gameDbTransactions.checkMove(0),Duration.Inf).createMoveRank()
    assert(moveRank.isInstanceOf[NeverTried])
  }

  test("Get positions on a few moves"){

    val moveRanksF =Seq(2, 162, 6).map(gameDbTransactions.checkMove(_)).map(_.map(_.createMoveRank()))
    val moveRanks =Await.result(Future.sequence(moveRanksF),Duration.Inf)
    assert(moveRanks.size==3)
    moveRanks.foreach(m=>assert(m.isInstanceOf[NeverTried]))

  }


  test("register loss"){
    val loss=Loss(1,1)
    Await.result(gameDbTransactions.registerLosingPathMove(loss),Duration.Inf)
  }

  test("process game at end"){
    val gameId=2.toLong
    val gameOutcome=lost
    val numberOfMoves=5
    val moves=Seq(Move(gameId,1,4),Move(gameId,3,20),Move(gameId,5,100))
    val s =Await.result(gameDbTransactions.processGameAtEnd(gameId,won,5,moves),Duration.Inf)
  }
}
