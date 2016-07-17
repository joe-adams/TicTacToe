package com.github.joeadams

import com.github.joeadams.game.GameSetter
import com.github.joeadams.model.{Coordinate, StatefulSquareImpl}
import com.github.joeadams.state.SquareState
import com.github.joeadams.ui.Board
import com.github.joeadams.aiengine.{Strategy, StrategyImpl}
import com.github.joeadams.persistance.service.Util
import com.github.joeadams.persistance.storage.{GamePersistence, MovePersistence}
import com.github.joeadams.ui.PopupHelper._
import rx.lang.scala.Observable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object ImportantValuesImpl extends ImportantValues{
  val yRange=(-1 to 1)
  val xRange=(-1 to 1)
  private val allCoordinatesVal =yRange.map(column=>
                        xRange.map(row=>
                          Coordinate(column,row))).flatten.toSet

  allCoordinatesVal.toList.sorted.foreach(c=>log(c.toString))


  val allStatefulSquaresVal= allCoordinatesVal.map((coordinate:Coordinate)=>{
    val columnIndexOnUI=coordinate.y+1
    val rowIndexOnUI=coordinate.x+1
    val button=Board.makeAButton(rowIndexOnUI,columnIndexOnUI)
    val statefulSquare=StatefulSquareImpl(coordinate,new SquareState(),button)
    coordinate->statefulSquare
  }).toMap
  override def allCoordinates=allCoordinatesVal
  private val coordinateByIdVal=allCoordinates.map((c)=>c.uniqueId->c).toMap
  override def coordinatesById=coordinateByIdVal
  override def allStatefulSquaresMap=allStatefulSquaresVal
  private val individualClickObservables: Iterable[Observable[Coordinate]] =allStatefulSquaresMap.values.map(_.clickObservable)
  override def clickListener =Observable.from(individualClickObservables).flatten

  override def getOpponent(gameId:Long)=new StrategyImpl(gameId)
  override def initialize={
    GameSetter.startObserving()
    GamePersistence.loadGames()
    MovePersistence.loadMoves()
  }
  override def log(s:String)={
    Util.append("log.txt",s)
  }




}
