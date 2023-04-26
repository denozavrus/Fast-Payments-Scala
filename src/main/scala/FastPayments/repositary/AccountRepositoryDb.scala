package FastPayments.repositary

import FastPayments.models.{Account, AddAccount, ReplenishItem, TransferItem, TransferResponse, UpdateAccount, WithdrawItem}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import FastPayments.db.AccountDb._
import FastPayments.db.CashbackDb._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends CheckRepositary with TransactionTypes {

  override def list(): Future[Seq[Account]] = {
    db.run(AccountTable.result)
  }

  override def create(add: AddAccount): Future[Account] = {
    val item = Account(username = add.username, sum = add.sum)
    for {
      _ <- db.run(AccountTable += item)
      res <- get(item.id)
    } yield res
  }

  def find(id: UUID): Future[Option[Account]] = {
    db.run(AccountTable.filter(_.id === id).result.headOption)
  }

  override def update(update: UpdateAccount): Future[Option[Account]] = {

    val query = AccountTable.filter(_.id === update.id)

    val updateQuery = (update.sum, update.username) match {
      case (Some(sum), Some(username)) => query.map(a => (a.sum, a.username)).update((sum, username))
      case (Some(sum), None) => query.map(a => a.sum).update(sum)
      case (None, Some(username)) => query.map(a => a.username).update(username)
    }

    db.run(updateQuery)
    find(update.id)
  }

  override def get(id: UUID): Future[Account] = {
    db.run(AccountTable.filter(_.id === id).result.head)
  }

  override def delete(id: UUID): Future[Unit] = Future {
    db.run(AccountTable.filter(_.id === id).delete).map(_ => ())
  }

  override def replenish(replenishItem: ReplenishItem): Future[Either[String, Account]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === replenishItem.id).map(x => x.sum).result.headOption)
      either: Either[String, Int] <- balance match {
        case Some(balance) =>
          db.run {
            AccountTable.filter(_.id === replenishItem.id).map(x => x.sum).update(balance + replenishItem.amount)
          }.map(Right(_))
        case None => Future.successful(Left("Такой элемент не найден"))
      }

      res <- either match {
        case Right(_) => find(replenishItem.id).map(maybeAccount => maybeAccount.map(account => Right(account)).getOrElse(Left("No such account")))
        case Left(error) => Future.successful(Left(error))
      }
    } yield res
  }

  override def withdraw(withdrawItem: WithdrawItem): Future[Either[String, Account]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === withdrawItem.id).map(x => x.sum).result.headOption)
      either: Either[String, Int] <- balance match {
        case Some(balance) if balance >= withdrawItem.amount =>
          db.run {
            AccountTable.filter(_.id === withdrawItem.id).map(x => x.sum).update(balance - withdrawItem.amount)
          }.map(Right(_))
        case Some(balance) if balance < withdrawItem.amount => Future.successful(Left("Недостаточно средств"))
        case None => Future.successful(Left("Такой элемент не найден"))
      }

      res <- either match {
        case Right(_) => find(withdrawItem.id).map(maybeAccount => maybeAccount.map(account => Right(account)).getOrElse(Left("No such account")))
        case Left(error) => Future.successful(Left(error))
      }
    } yield res
  }

  def getCashback(catid: UUID): Future[Float] = {
    for {
      result <- db.run(CashbackTable.filter(_.id === catid).map(x => x.percent).result.headOption)
      percent = result match {
        case Some(percent) => percent
        case None => 0
      }
    } yield percent
  }

  override def transfer(transferItem: TransferItem): Future[Either[String, TransferResponse]] = {
    for {
      withdrawRes <- withdraw(WithdrawItem(transferItem.from, transferItem.amount))
      result <- withdrawRes match {
        case Right(rightW) =>
          {
            transferItem.categoryid match {
              case Some(cat) => replenish(ReplenishItem(transferItem.from, Await.result(getCashback(cat), 10.seconds) * transferItem.amount))
              case None => {}
            }
            replenish(ReplenishItem(transferItem.to, transferItem.amount)).map {
              replenishRes =>
                replenishRes.map { rightR =>
                  TransferResponse(rightW, rightR)
                }
            }
          }

        case Left(error) => Future.successful(Left(error))
      }
    } yield result
  }
}

