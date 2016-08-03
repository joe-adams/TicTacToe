package com.github.joeadams.service.dao

import com.github.joeadams.service._
import com.github.joeadams.service.dao.MoveHistory.{MoveRank, _}
import com.github.joeadams.service.dao.Tables.GameMove

import scala.reflect.ClassTag

case class MoveHistory(moveList:Seq[GameMove], lossLevel:Option[Int], win:Boolean) {

  lazy val average=if(moveList.isEmpty) None else Some(findAverage(moveList))

  def createMoveRank():MoveRank = this match {
    case MoveHistory(_,_,true)=>InstantWin()
    case MoveHistory(Nil,_,_)=>NeverTried()
    case MoveHistory(m,Some(x),_)=>HasLossRank(average.get,x)
    case _ if average.get>=(1.0/moveListSampleSize*2) =>WinAverage(average.get)
    case _ if average.get>=(-1.0/moveListSampleSize*2) =>DrawAverage(average.get)
    case _=>LoseAverage(average.get)
  }

}

object MoveHistory{

  val moveListSampleSize=10
  trait MoveRank extends Ordered[MoveRank]{
    def rank:Int
    override def compare(that:MoveRank):Int=moveRankOrder.compare(this,that)
  }
  trait HasAverage extends MoveRank {
    def average:Double
  }

  case class InstantWin() extends MoveRank{ val rank=60}
  case class WinAverage(average:Double) extends MoveRank with HasAverage{ val rank=50}
  case class NeverTried() extends MoveRank{ val rank=40}
  case class DrawAverage(average:Double) extends MoveRank with HasAverage{ val rank=30}
  case class LoseAverage(average:Double) extends HasAverage{ val rank=20}
  case class HasLossRank(average:Double,lossRank:Int) extends  HasAverage{ val rank=10}


  val rankOrderFull:Ordering[MoveRank]=Ordering.by(_.rank)
  val averageOrderFull:Ordering[HasAverage]=Ordering.by(_.average)
  val lossOrderFull:Ordering[HasLossRank]=Ordering.by(_.lossRank)
  def partialOrder[R<:MoveRank  :ClassTag](ordering:Ordering[R]):PartialOrdering[MoveRank]={
    new PartialOrdering[MoveRank]{
      override def tryCompare(x: MoveRank, y: MoveRank): Option[Int] = (x,y) match {
        case (x:R,y:R)=> noZero(ordering.compare(x,y))
        case _=>None
      }

      private def noZero(v:Int)=if (v==0) None else Some(v)

      override def lteq(x: MoveRank, y: MoveRank): Boolean = (x,y) match {
        case (x:R,y:R)=> lt(x,y)
        case _=>false
      }
    }
  }

  val rankOrder =partialOrder(rankOrderFull)
  val lossOrder =partialOrder(lossOrderFull)
  val averageOrder=partialOrder(averageOrderFull)
  val moveRankSeq =Seq(rankOrder,lossOrder,averageOrder)


  val moveRankOrder:Ordering[MoveRank]=new Ordering[MoveRank]() {
    override def compare(x: MoveRank, y: MoveRank): Int = moveRankSeq.flatMap(_.tryCompare(x, y)).headOption.getOrElse(0)
  }

  def gameScore(gm:GameMove): Int ={
    val remainingMoves=gm.game.numberOfMoves-gm.move.moveNumber
    val multiplier=if (gm.game.outcome==lost) -1 else 1
    remainingMoves*multiplier
  }


  def findAverage(moveList:Seq[GameMove]): Double ={
    val sample=moveList.take(moveListSampleSize).map(gameScore)
    sample.reduce(_+_).toDouble /sample.size
  }
}
