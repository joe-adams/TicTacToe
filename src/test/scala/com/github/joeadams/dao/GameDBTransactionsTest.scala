package com.github.joeadams.dao


import com.github.joeadams.service.dao.GameDbTransactions
import com.github.joeadams.service.dao.MoveHistory.NeverTried
import com.github.joeadams.service.dao.Tables.{Loss, Move}
import com.github.joeadams.service._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import com.github.joeadams.service.dao.slickapi._
import slick.jdbc.meta._

import scala.concurrent.Future

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class GameDBTransactionsTest   extends FunSuite with BeforeAndAfter with ScalaFutures {

  before{ ClearData.clear()}
  after{ ClearData.clear()}



  ignore("Get positions on no positions"){
   // ClearData.clear()
    val gameDbTransactions: GameDbTransactions=GameDbTransactions()
    val a: Seq[Unit] =gameDbTransactions.ensureAllTables()
    val moveRank =gameDbTransactions.checkMove(0).createMoveRank()
    assert(moveRank.isInstanceOf[NeverTried])
  }


  test("register loss"){
    ClearData.clear()
    val gameDbTransactions: GameDbTransactions=GameDbTransactions()
    gameDbTransactions.ensureAllTables()
    val loss=Loss(1,1)
    val s: Int =gameDbTransactions.registerLosingPathMove(loss)
    println(s)

  }

  ignore("process game at end"){
    val gameDbTransactions: GameDbTransactions=GameDbTransactions()
    val a: Seq[Unit] =gameDbTransactions.ensureAllTables()
    val gameId=2
    val gameOutcome=lost
    val numberOfMoves=5
    val moves=Seq(Move(gameId,1,4),Move(gameId,3,20),Move(gameId,5,100))
    val s=gameDbTransactions.processGameAtEnd(1,won,5,moves)

  }
}
