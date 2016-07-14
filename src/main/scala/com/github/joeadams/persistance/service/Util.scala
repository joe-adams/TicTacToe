package com.github.joeadams.persistance.service

import java.io.{File, FileWriter}

import com.github.joeadams.Cases
import com.github.joeadams.Cases._

import scala.io.Source

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object Util {

  def separator="|"

  def parseXO(xo:String)=if(xo=="X")X else O

  def xoToString(xo:X_OR_O):String=if (xo==X) "X" else "O"

  def parseGameOutcome(outcome:String)=outcome match {
    case "X"=>WonBy(X)
    case "O"=>WonBy(O)
    case _ => draw
  }

  def gameOutcomeToString(outcome:GameOutcome):String=outcome match {
    case WonBy(xo)=>xoToString(xo)
    case draw => "DRAW"
  }

  def readFile[O](file:File,lineReader:String=>O)={
    val source=Source.fromFile(file)
      source.getLines().toSeq.map(lineReader)
  }

  def append(filename:String,line:String)={
    val write=new FileWriter(filename,true)
    try{
      write.append(line+"\n")
    } finally{
      write.close()
    }
  }
}
