package com.github.joeadams.service.persistance

import java.io.File

import scala.collection.mutable


/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
object GamePersistence {


  def getGames() = gameList

  def loadGames(): Unit = {
    val file = new File(fileName)
    if (file.exists()) {
      val games = Util.readFile(file, parse(_))
      games.foreach(g => {
        gameList += g
      })
    }
  }

  def addGame(game: Game) = {
    val stringToStore = gameToStorageString(game)
    Util.append(fileName, stringToStore)
    gameList += game
  }

  private def addGameToFile(game: Game) = {
    val id = game.id.toString
    val computerAs = Util.xoToString(game.computerAs)
    val outcome = Util.gameOutcomeToString(game.outcome)
    val moves = game.moves.toString
    val stringToStore = Seq(game.id.toString, computerAs, moves).mkString(Util.separator.toString)
    Util.append(fileName, stringToStore)
  }


  private def parse(line: String): Game = {
    val split = line.split(Util.separator)
    val id = split(0).toInt
    val computerAs = Util.parseXO(split(1))
    val outcome = Util.parseGameOutcome(split(2))
    val moves = split(3).toInt
    Game(id, computerAs, outcome, moves)
  }

  private def gameToStorageString(game: Game): String = {
    val id = game.id.toString
    val computerAs = Util.xoToString(game.computerAs)
    val outcome = Util.gameOutcomeToString(game.outcome)
    val moves = game.moves.toString
    Seq(id, computerAs, outcome, moves).mkString(Util.separator)
  }

  private val fileName = "gamerecords.txt"

  private val gameList = new mutable.MutableList[Game]()

}
