package com.github.joeadams.service.dao

import com.github.joeadams.service.dao.slickapi._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait Transactor {
  def transaction[R](inTransaction:DbAction=>Future[R]):R
}


object Transactor{
  def apply(): Transactor = new Default()

  class Default extends Transactor{
   sealed case class DbActionCase(db:Database,inner:InnerDbAction=InnerDbAction()) extends DbActionWithComponents{
     println("DbActionCase starts existence")
   }
   override def transaction[R](inTransaction:DbAction=>Future[R]):R={
     println("transactor")
     val db=Database.forConfig("conf.database")
     val dbActionCase=DbActionCase(db)
     try{
       println("yo")
       val future: Future[R] =inTransaction(dbActionCase)
       val result=Await.result(future,Duration.Inf)
       println("t")
       result
     }catch {
       case e:Throwable=>println(e)
         e.printStackTrace()
         throw e
     } finally db.close()
   }
  }
}