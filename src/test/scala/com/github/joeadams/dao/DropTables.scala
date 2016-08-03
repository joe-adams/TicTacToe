package com.github.joeadams.dao

import com.github.joeadams.dao.slickapi._



object DropTables extends TestGameDbTransactions{

  def drop()=transaction(x=> db.run(sqlu"DROP ALL OBJECTS"))


}
