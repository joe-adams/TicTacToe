package com.github.joeadams.service.dao

import com.github.joeadams.service._
import com.github.joeadams.service.dao.Tables._
import com.github.joeadams.service.dao.slickapi._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait GameDbService {
  def processGameAtEnd(gameId:Long, gameOutcome:GameOutcome, numberOfMoves:Int, moves:Seq[Move],dbAction: DbAction):Future[Any]
  def checkMove(boardPosition:Int,dbAction: DbAction): Future[MoveHistory]
  def registerLosingPathMove(loss:Loss,dbAction: DbAction): Future[Int]
  def ensureAllTables(dbAction: DbAction):  Future[Seq[Unit]]

}

object GameDbService {

  trait Default extends GameDbService {
    override def processGameAtEnd(gameId: Long, gameOutcome: GameOutcome, numberOfMoves: Int, moves: Seq[Move], dbAction: DbAction) = {
      val game = Game(gameId, gameOutcome.toString, numberOfMoves)
      val gameFuture: Future[Int] = dbAction.addGame(game)
      val moveFuture: Future[Option[Int]] = dbAction.addMoves(moves)
      val lastMove: Move = moves.last
      val outComeFuture: Future[Any] = gameOutcome match {
        case won: COMPUTER_WON => addWinIfNecessary(lastMove.newBoardPosition, dbAction)
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

    private def addWinIfNecessary(board: Int, dbAction: DbAction): Future[AnyVal] = {
      for {
        registered: Boolean <- dbAction.getWin(board)
        r <- if (!(registered)) {
          dbAction.addWin(board)
        } else {
          Future.successful(())
        }
      } yield r
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

    override def ensureAllTables(dbAction: DbAction) = Future.sequence(Tables.allTheTables.map(t=>ensureATable(t, dbAction)))

    private def ensureATable(table: TableQuery[_ <: Table[_]], dbAction: DbAction): Future[Unit] =
      dbAction.getTables(table).flatMap(r=>{if (r.isEmpty) dbAction.tableCreate(table) else Future.successful(())})
  }
}