package com.github.joeadams.board

import com.github.joeadams.service.board.{Board, Coordinate}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSuite}
import com.github.joeadams.service._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class BoardTest  extends FunSuite with BeforeAndAfter {

  test("Board converts to number"){
    val map=Map(Coordinate(0,0)->X)
    val board=Board.fromMap(map)
    val n=board.toInt
    assert(n==162)
  }

  test("Board converts to number2"){
    val map=Map(Coordinate(0,0)->X,Coordinate(1,1)->O)
    val board=Board.fromMap(map)
    val n=board.toInt
    assert(n==6723)
  }

  test("Number converts to Board"){
    val board=Board.fromInt(162)
    assert(!board.s.exists(_==O))
    assert(board.s.filter(_==blank).size==8)
    val xp=board.kv.find(_.s==X).map(_.c).get
    assert(xp==Coordinate(0,0))
  }

  test("Number converts to Board2"){
    val board=Board.fromInt(6723)
    val xp=board.kv.find(_.s==X).map(_.c).get
    assert(xp==Coordinate(0,0))
    val op=board.kv.find(_.s==O).map(_.c).get
    assert(op==Coordinate(1,1))
  }


}
