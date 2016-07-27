package com.github.joeadams.service.dao

import com.github.joeadams.service.dao.slickapi._


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait Transactor {
  def transaction[R](inTransaction:DbAction=>R):R
}


object Transactor{
  def apply(): Transactor = new Default()

  class Default extends Transactor{
   sealed case class DbActionCase(db:Database,inner:InnerDbAction=InnerDbAction()) extends DbActionWithComponents
   override def transaction[R](inTransaction:DbAction=>R):R={
     println("transactor")
     val db=Database.forConfig("conf.database")
     val dbActionCase=DbActionCase(db)
     try{
       println("yo")
       val t=inTransaction(dbActionCase)
       println("t")
       t
     }finally db.close()
   }
  }
}