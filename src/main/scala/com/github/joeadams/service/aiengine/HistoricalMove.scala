package com.github.joeadams.service.aiengine

import com.github.joeadams.service.log
import com.github.joeadams.service.persistance.{Game, GamePersistence, Move, MovePersistence}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */

case class HistoricalMove(moveAsInt:Int,losingStreak:Int,average:Double) extends Ordered[HistoricalMove] {
  val streakOrder=Ordering.by((_:HistoricalMove).losingStreak).reverse
  val averageOrder=Ordering.by((_:HistoricalMove).average)
  override def compare(that: HistoricalMove): Int = {
    val sCompare=streakOrder.compare(this,that)
    if (sCompare!=0) sCompare else averageOrder.compare(this,that)
  }
}

object HistoricalMove {
  val losingGracePeriod=10
  val sampleSize=10
  case class MoveWithGame(move:Move,game:Game)  extends Ordered[MoveWithGame] {
    override def compare(that: MoveWithGame): Int = Ordering.by((moveWithGame: MoveWithGame)=>game.id).reverse.compare(this,that)
  }

  def makeFromHistory(moveAsInt:Int,boardPositionAsInt:Int):HistoricalMove={
    val moves=MovePersistence.getMoves().filter(move => (move.boardPosition == boardPositionAsInt)&&(move.moveTaken==moveAsInt))
    val movesWithGames=movesToMoveWithGames(moves)
    val r=if (movesWithGames.size==0) {
      HistoricalMove(moveAsInt,0,0.toDouble)
    } else{
      val losingStreakWithoutGracePeriod=movesWithGames.takeWhile(_.game.scoreVal<0).size
      val losingStreak=Seq((losingStreakWithoutGracePeriod-losingGracePeriod),0).max
      val sample =movesWithGames.take(sampleSize).map(_.game.score)
      val average=findAverage(sample)
      HistoricalMove(moveAsInt,losingStreak,average)
    }
    r
  }

  def findAverage(sample:Seq[Int]):Double={
    val size = sample.size
    val sum = sample.reduce(_ + _).toDouble
    sum/size
  }

  def movesToMoveWithGames(moves:Seq[Move]):Seq[MoveWithGame]={
    val moveWithGames: Seq[MoveWithGame] =moves.map(move=>{
      val gameOpt=GamePersistence.getGames().find(game=>game.id==move.gameId)
      if (gameOpt.isEmpty){
        Option.empty
      } else{
        Some(MoveWithGame(move,gameOpt.get))
      }
    }).flatten
    moveWithGames
  }




}
