package com.github.joeadams.service.board

import com.github.joeadams.service.{SquareMarking, _}

import scala.language.implicitConversions

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */


object Board {

  case class KV(c:Coordinate,s:SquareMarking)
  implicit def kvfromTup=KV.tupled

  implicit def fromMap(map:Map[Coordinate,SquareMarking])=Board(map)
  implicit def fromMutableMap(map:scala.collection.mutable.Map[Coordinate,SquareMarking])=Board(map.toMap)

  def int2Square(input: Int):SquareMarking = input match {
    case i if i == 2 => X
    case i if i == 1 => O
    case i if i == 0 => blank
  }



  sealed case class Board(map:Map[Coordinate, SquareMarking]) {

    def transform(f:Board=>Board):Board=f(this)

    def mutable=scala.collection.mutable.Map()++map


    def kv=map.iterator.map(KV.tupled(_))
    def s=map.values

    def + (kv:KV)(implicit conv:Tuple2[Int,Int]=>Coordinate):Board=map+ (kv.c->kv.s)

    def toInt={
      val r=map.map(KV.tupled).map(kv=>{
        val markingValue=kv.s.asInt
        val coordinateValue=threePower(kv.c.id)
        val s=markingValue*coordinateValue
        s
      }).fold(0)(_+_)
      r
    }



    def asString:String={
      Coordinate.yRange.map(y=>{
        Coordinate.xRange.map(x=>map(x,y)).mkString("|")
      }).mkString("\n")
    }
  }


  def fromInt(i:Int):Board={
    val map=Coordinate.id.keySet.map(id=>{
      val marking=squareFromInt(id,i)
      val coordinate=Coordinate.id(id)
      coordinate->marking
    }).toMap
    Board(map)
  }
  val blankBoard=Board(Coordinate.allCoordinates.map(_->blank).toMap)



  private def squareFromInt(id:Int,boardNumber:Int):SquareMarking={
    val reduced=boardNumber/threePower(id)
    val i=reduced%3
    int2Square(i)
  }

  private val threePower =(0 to 8).map(i=>i->Math.pow(3, i).toInt).toMap



}
