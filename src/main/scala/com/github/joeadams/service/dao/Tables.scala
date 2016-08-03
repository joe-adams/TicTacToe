package com.github.joeadams.service.dao

import slick.lifted.ProvenShape
import com.github.joeadams.service.dao.slickapi._
import com.github.joeadams.service._

object Tables {
  case class GameMove(game:Game,move:Move)

  case class Game(id:Long, outcomeString:String, numberOfMoves:Int){
    def outcome=stringToGameOutcome(outcomeString)
  }
  class Games(tag:Tag) extends Table[Game](tag,"GAME"){
    def id=column[Long]("ID",O.PrimaryKey)
    def outcomeString=column[String]("OUTCOME")
    def numberOfMoves=column[Int]("NUMBER_OF_MOVES")
    def * : ProvenShape[Game]=(id,outcomeString,numberOfMoves) <>(Game.tupled, Game.unapply)
  }
  val games= TableQuery[Games]

  case class Move(gameId:Long,moveNumber:Int,newBoardPosition:Int)
  class Moves(tag:Tag) extends Table[Move](tag,"MOVE"){
    def gameId=column[Long]("GAME_ID")
    def moveNumber=column[Int]("MOVE_NUMBER")
    def newBoardPosition=column[Int]("NEW_BOARD_POSITION")
    def * : ProvenShape[Move]= (gameId,moveNumber,newBoardPosition)<>(Move.tupled,Move.unapply)
    def pk=primaryKey("pk_mv",(gameId,moveNumber))
  }
  val moves=TableQuery[Moves]

  case class Loss(position:Int,level:Int)
  class Losses(tag:Tag) extends Table[Loss](tag,"LOSS"){
    def position=column[Int]("POSITION",O.PrimaryKey)
    def level=column[Int]("LEVEL")
    def * : ProvenShape[Loss]=(position,level)<>(Loss.tupled,Loss.unapply)
  }
  val losses=TableQuery[Losses]

  class Wins(tag:Tag) extends Table[Int](tag,"WIN"){
    def position=column[Int]("POSITION",O.PrimaryKey)
    def * : ProvenShape[Int]=(position)
  }
  val wins=TableQuery[Wins]

  val allTheTables:Seq[TableQuery[_<:Table[_]]]=Seq(games,moves,losses,wins)

}
