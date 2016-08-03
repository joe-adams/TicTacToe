package com.github.joeadams.dao

import com.github.joeadams.service.dao.{GameDbTransactions, Transactor}
import com.github.joeadams.service.dao.slickapi._



trait TestTransactor extends Transactor.Default{
  override val dbSource=Database.forConfig("conf.databaseTest")
}
class TestGameDbTransactions extends GameDbTransactions.DefaultClass with TestTransactor{
  def db=dbSource
}
