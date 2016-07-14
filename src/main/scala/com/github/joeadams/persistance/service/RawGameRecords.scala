package com.github.joeadams.persistance.service

import com.github.joeadams.Cases.{GameOutcome, SquareMarking, X_OR_O}
import com.github.joeadams.model.Coordinate
import com.github.joeadams.TicTacToe.TTT

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

object RawGameRecords{
  case class RawMove(computerIs:X_OR_O, board:Map[Coordinate,SquareMarking], move:Coordinate)
  var recordOpt:Option[RawGameRecordBuilder]=Option.empty

  def addMove(id:Long, computerIs:X_OR_O, board:Map[Coordinate,SquareMarking], move:Coordinate)={
      val recordBuilder:RawGameRecordBuilder=getOrMakeRecordBuilder(id,computerIs)
      recordBuilder.addMove(computerIs,board,move)

  }

  def getMoveList=recordOpt.get.getList()

  def processOutcome(id:Long,computerIs:X_OR_O,gameOutcome: GameOutcome,numberOfMoves:Int,transforms: BoardTransforms.Transform):RawGameRecord={
    val recordBuilder:RawGameRecordBuilder=getOrMakeRecordBuilder(id,computerIs)
    val record=recordBuilder.build(gameOutcome,numberOfMoves,transforms)
    record
  }




 case class RawGameRecord(id:Long, computerIs:X_OR_O, moveList:List[RawMove], gameOutcome: GameOutcome, numberOfMoves:Int, transform: BoardTransforms.Transform)

  /**
    * Not a true builder pattern, but a sort of proto-object that appears before our true object
    *
    * @param id
    * @param computerIs
    */
  case class RawGameRecordBuilder(id:Long,computerIs:X_OR_O){
    private val moveList= ListBuffer.empty[RawMove]
    def addMove(computerIs:X_OR_O,board:Map[Coordinate,SquareMarking], move:Coordinate):Unit={
      moveList+=RawMove(computerIs, board, move)
    }
    def getList():List[RawMove]=moveList.toList

    def build(outcome:GameOutcome,numberOfMoves:Int,transforms: BoardTransforms.Transform):RawGameRecord=RawGameRecord(id,computerIs,getList() ,outcome,numberOfMoves,transforms)
  }


  private def getOrMakeRecordBuilder(id:Long,computerIs:X_OR_O):RawGameRecordBuilder={
    recordOpt match {
      case None => {
        recordOpt = Some(RawGameRecordBuilder(id, computerIs))
        recordOpt.get
      }
      case Some(RawGameRecordBuilder(x, y)) => {
        if(x==id)
          recordOpt.get
        else {
          recordOpt = Some(RawGameRecordBuilder(id, computerIs))
          recordOpt.get
        }
      }
    }

  }
}
