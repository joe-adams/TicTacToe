package com.github.joeadams.aiengine

import com.github.joeadams.dao.{DropTables, TestGameDbTransactions}
import com.github.joeadams.service.aiengine.StrategyImpl
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import com.github.joeadams.service._
import com.github.joeadams.service.board.{Board, BoardTransforms, Coordinate}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class StrategyImplTest extends FunSuite with BeforeAndAfterAll  {

  override def beforeAll={
    new TestGameDbTransactions().ensureAllTables()
    Thread.sleep(5000)
  }


  test("test Strategy"){
    val strategy=StrategyImpl(5.toLong,X,BoardTransforms(),()=>new TestGameDbTransactions)
    val m: Coordinate =strategy.move(Board.blankBoard)
  }

}
