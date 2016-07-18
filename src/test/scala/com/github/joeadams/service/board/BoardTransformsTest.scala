package com.github.joeadams.service.board

import org.scalatest.{FlatSpec, Matchers}

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
class BoardTransformsTest  extends FlatSpec with Matchers {

  val boardTransforms=BoardTransforms()
  "Transform Matrix" should "transform correctly" in {
    Coordinate.allCoordinates.foreach((coordinate)=>{
      val coordinateAsMatrix=coordinate.asMatrix
      println(coordinateAsMatrix)
      boardTransforms.getAllBoardFlips.foreach((flip)=>{
        val flipped=coordinateAsMatrix*flip.flip
        println(flipped)
        val back=flipped*flip.reverseFlip
        println("back: "+back)
        back should be (coordinateAsMatrix)
      })
    })
  }



}
