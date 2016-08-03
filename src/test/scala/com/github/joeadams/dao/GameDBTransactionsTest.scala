package com.github.joeadams.dao


import com.github.joeadams.service._
import com.github.joeadams.service.dao.MoveHistory.NeverTried
import com.github.joeadams.service.dao.Tables.{Loss, Move}
import org.scalatest._





class GameDBTransactionsTest   extends FunSuite with BeforeAndAfterAll{
  def gameDbTransactions=new TestGameDbTransactions

  override def beforeAll={
      DropTables.drop()
      Thread.sleep(5000)
      new TestGameDbTransactions().ensureAllTables()
      Thread.sleep(5000)
  }

  test("ensure tables"){
    new TestGameDbTransactions().ensureAllTables()
    Thread.sleep(5000)
  }


  test("Get positions on no positions"){
    val moveRank =gameDbTransactions.checkMove(0).createMoveRank()
    assert(moveRank.isInstanceOf[NeverTried])
  }

  test("Get positions on a few moves"){

    val moveRanks =Seq(2, 162, 6).map(gameDbTransactions.checkMove(_)).map(_.createMoveRank())
    assert(moveRanks.size==3)
    moveRanks.foreach(m=>assert(m.isInstanceOf[NeverTried]))

  }


  test("register loss"){
    val loss=Loss(1,1)
    gameDbTransactions.registerLosingPathMove(loss)
  }

  test("process game at end"){
    val gameId=2.toLong
    val gameOutcome=lost
    val numberOfMoves=5
    val moves=Seq(Move(gameId,1,4),Move(gameId,3,20),Move(gameId,5,100))
    val s =gameDbTransactions.processGameAtEnd(gameId,won,5,moves)
  }
}
