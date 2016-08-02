package com.github.joeadams.service.dao


import com.github.joeadams.service.dao.slickapi._

import scala.concurrent._
import scala.concurrent.duration.Duration



/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait Transactor {
  def transaction[R](inTransaction:DbAction=>Future[R]):Future[R]
  def dbSource:Database
}


object Transactor{


  trait Default extends Transactor {

    sealed case class DbActionCase(db: Database=dbSource, inner: InnerDbAction = InnerDbAction()) extends DbActionWithComponents

    override def dbSource = Database.forConfig("conf.database")


    override def transaction[R](inTransaction: DbAction => Future[R]): Future[R] = Future.successful(transactionBlock(inTransaction))



    def transactionBlock[R](inTransaction: DbAction => Future[R],dbAction: DbActionWithComponents=DbActionCase()): R ={
        try {
          Await.result(inTransaction(dbAction), Duration.Inf)
        } finally {
          dbAction.db.close()
        }
      }

  }
}