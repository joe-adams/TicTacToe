package com.github.joeadams.dao


import com.github.joeadams.service.dao.{GameDbTransactions, Transactor}
import com.github.joeadams.service.dao.slickapi._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

trait TestTransactor extends Transactor.Default{
  override val dbSource=Database.forConfig("conf.databaseTest")
}
class TestGameDbTransactions extends GameDbTransactions.DefaultClass with TestTransactor{
  def db=dbSource


}
