package com.github.joeadams.service.dao

import com.github.joeadams.service.dao.slickapi._

import scala.concurrent._
import scala.concurrent.duration.Duration

trait Transactor {
  def transaction[R](inTransaction:DbAction=>Future[R]):R
  def dbSource:Database
}

object Transactor{

  trait Default extends Transactor {

    sealed case class DbActionCase(db: Database=dbSource, inner: InnerDbAction = InnerDbAction()) extends DbActionWithComponents

    override def dbSource = Database.forConfig("conf.database")

    override def transaction[R](inTransaction: DbAction => Future[R]): R = transactionBlock(inTransaction)

    def transactionBlock[R](inTransaction: DbAction => Future[R],dbAction: DbActionWithComponents=DbActionCase()): R =
        try {
          Await.result(inTransaction(dbAction), Duration.Inf)
        } finally {
          dbAction.db.close()
        }

  }
}