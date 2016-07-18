package com.github.joeadams.service.board

import com.github.joeadams.service._

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object Util {

  def stringToBoard(in:String):Board={
    val seq=in.replace("\n","").toCharArray.toSeq
    val marking=seq.map(_ match {
      case 'x'=>X
      case 'o'=>O
      case _=>blank
    })
    (0 to 8).map(index=>{
      val coordinate=Coordinate.fromId(index)
      coordinate->marking(index)
    }).toMap
  }

}
