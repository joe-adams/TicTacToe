package com.github.joeadams.service.dao

import com.github.joeadams.service.dao.slickapi._
import com.github.joeadams.service._
import com.github.joeadams.service.dao.Tables._
import com.github.joeadams.service.dao._
import org.h2.table.Table
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * The files look weird if this is blank.  Important company owns this code. Don't format
  * this wrong or we're going to have a problem.
  */
trait GameDbService {
  def processGameAtEnd(gameId:Long, gameOutcome:GameOutcome, numberOfMoves:Int, moves:Seq[Move],dbAction: DbAction):Future[Seq[Any]]
  def checkMove(boardPosition:Int,dbAction: DbAction): Future[MoveHistory]
  def registerLosingPathMove(loss:Loss,dbAction: DbAction): Future[Int]
  def ensureAllTables(dbAction: DbAction): Future[Seq[Unit]]

}

object GameDbService {


  trait Default extends GameDbService {
    override def processGameAtEnd(gameId: Long, gameOutcome: GameOutcome, numberOfMoves: Int, moves: Seq[Move], dbAction: DbAction) = {
      val game = Game(gameId, gameOutcome.toString, numberOfMoves)
      val gameFuture: Future[Int] = dbAction.addGame(game)
      val moveFuture: Future[Option[Int]] = dbAction.addMoves(moves)
      val lastMove: Move = moves.last
      val outComeFuture: Future[Any] = gameOutcome match {
        case won: COMPUTER_WON => dbAction.addWin(lastMove.newBoardPosition)
        case lost: COMPUTER_LOST => registerLosingPathMove(Loss(lastMove.newBoardPosition, 1), dbAction)
        case _: Any => Future.successful(())
      }
      Future.sequence(Seq(gameFuture, moveFuture, outComeFuture))
    }


    override def checkMove(boardPosition: Int, dbAction: DbAction) = {
      val moveListFuture: Future[Seq[(Game, Move)]] = dbAction.getGameMove(boardPosition)
      val winFuture = dbAction.getWin(boardPosition)
      val lossFuture = dbAction.getLoss(boardPosition)
      for {
        moveListTuple: Seq[(Game, Move)] <- moveListFuture
        win: Boolean <- winFuture
        loss: Option[Int] <- lossFuture
        moveList: Seq[GameMove] = moveListTuple.map(GameMove.tupled).sortBy(_.game.id).reverse
      } yield MoveHistory(moveList, loss, win)
    }

    override def registerLosingPathMove(loss: Loss, dbAction: DbAction) = {
      for {
        optionalInt <- dbAction.getLoss(loss.position)
        result <- optionalInt match {
          case None => dbAction.addLoss(loss)
          case Some(x) => dbAction.updateLossIfToLowerLevel(loss)
        }
      } yield result
    }

    def ensureAllTables(dbAction: DbAction) = {
      val futures=allTheTables.map((table) => {
        dbAction.getTables(table).flatMap(r => {
          if (r.isEmpty) {
            dbAction.tableCreate(table)
          } else {
            Future.successful(())
          }
        })
      })
      Future.sequence(futures)
    }




  }

}
