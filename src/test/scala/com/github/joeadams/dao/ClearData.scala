package com.github.joeadams.dao

import com.github.joeadams.dao.slickapi._
import com.github.joeadams.service.dao.GameDbTransactions

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object ClearData {

  def clear(): Unit ={
    val db=Database.forConfig("conf.database")
    try{
      db.run(sqlu"select truncate_ttt();")
    }finally db.close()

    GameDbTransactions().ensureAllTables()

    println("hi")
  }




}
