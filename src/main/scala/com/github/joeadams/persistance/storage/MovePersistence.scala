package com.github.joeadams.persistance.storage

import java.io.File

import com.github.joeadams.TicTacToe._
import com.github.joeadams.persistance.service.Util

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object MovePersistence {

  def getMoves()=moveList

  def loadMoves():Unit ={
    val file=new File(fileName)
    if (file.exists()){
      val moves=Util.readFile(file,parse(_))
      moves.foreach(m=>{
        moveList+=m
      })

    }
  }

  def addMoves(moves:Seq[Move]) ={
    val strings=moves.map(m=>moveToStorageString(m))
    strings.foreach(s=>Util.append(fileName,s))
    moveList ++= moves.toIterator
  }


  private def parse(line:String):Move={
    val split=line.split(Util.separator)
    val gameId=split(0).toInt
    val moveNumber=split(1).toInt
    val playedAs=Util.parseXO(split(3))
    val boardPosition=split(4).toInt
    val moveTaken=split(5).toInt
    Move(gameId,moveNumber,playedAs,boardPosition,moveTaken)
  }

  private def moveToStorageString(move:Move)={
    val gameId=move.gameId.toString
    val moveNumber=move.moveNumber.toString
    val playedAs=Util.xoToString(move.playedAs)
    val boardPosition=move.boardPosition.toString
    val moveTaken=move.moveTaken.toString
    Seq(gameId,moveNumber,playedAs,boardPosition,moveTaken).mkString(Util.separator)
  }

  private val fileName="moverecords.txt"

  private val moveList=new mutable.MutableList[Move]()
}
