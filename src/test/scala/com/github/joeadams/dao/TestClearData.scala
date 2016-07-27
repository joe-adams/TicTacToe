package com.github.joeadams.dao

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class TestClearData extends FunSuite with BeforeAndAfter with ScalaFutures  {

  test("clear data"){
    ClearData.clear()
  }
}
