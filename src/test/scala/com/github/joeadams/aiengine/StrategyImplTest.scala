package com.github.joeadams.aiengine

import com.github.joeadams.dao.TestGameDbTransactions
import com.github.joeadams.service._
import com.github.joeadams.service.aiengine.StrategyImpl
import com.github.joeadams.service.board.{Board, BoardTransforms, Coordinate}
import org.scalatest.{BeforeAndAfterAll, FunSuite}


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
