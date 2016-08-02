package com.github.joeadams.dao

import com.github.joeadams.dao.slickapi._

import scala.concurrent.Await
import scala.concurrent.duration.Duration


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object DropTables extends TestGameDbTransactions{

  def drop():Unit = {
    val s: Int =Await.result(transaction(x=> db.run(sqlu"DROP ALL OBJECTS")),Duration.Inf)
    println(s"s: $s")
  }


}
