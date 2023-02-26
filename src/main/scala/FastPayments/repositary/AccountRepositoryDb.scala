package FastPayments.repositary

import FastPayments.models.{Account, AddAccount, ReplenishItem, TransferItem, UpdateAccount, WithdrawItem}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import FastPayments.db.AccountDb._

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
      //      case _ => query
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
      either: Either[String, Account] <- balance match {
        case Some(balance) =>
          db.run {
            AccountTable.filter(_.id === replenishItem.id).map(x => x.sum).update(balance + replenishItem.amount)
          }
        case None => Future.successful(Left("Такой элемент не найден"))
      }

      res <- either match {
        case Right(_) => find(replenishItem.id).map(maybeAccount => maybeAccount.map(account => Right(account)).getOrElse(Left("No such account")))
        case left: Left[String, _] => Future.successful(left)
      }
    } yield res
  }

  override def withdraw(withdrawItem: WithdrawItem): Future[Either[String, Account]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === withdrawItem.id).map(x => x.sum).result.headOption)
      either: Either[String, Account] <- balance match {
        case Some(balance) if balance >= withdrawItem.amount =>
          db.run {
            AccountTable.filter(_.id === withdrawItem.id).map(x => x.sum).update(balance - withdrawItem.amount)
          }
        case Some(balance) if balance < withdrawItem.amount => Future.successful(Left("Недостаточно средств"))
        case None => Future.successful(Left("Такой элемент не найден"))
      }

      res <- either match {
        case Right(_) => find(withdrawItem.id).map(maybeAccount => maybeAccount.map(account => Right(account)).getOrElse(Left("No such account")))
        case Left: Left[String, _] => Future.successful (Left)
      }
    } yield res
  }

  override def transfer(transferItem: TransferItem): Future[Either[String, Seq[Account]]] = {
//    for {
//      balance <- db.run(AccountTable.filter(_.id === transferItem.from).map(x => x.sum).result.headOption)
//      either: Either[String, Seq[Account]] <- balance match {
//        case Some(balance) if balance >= transferItem.amount =>
//            db.run {
//              AccountTable.filter(_.id === transferItem.from).map(x => x.sum).update(balance - transferItem.amount)
//            }
//            replenish(ReplenishItem(transferItem.to, transferItem.amount))
//
//        case Some(balance) if balance < transferItem.amount => Future.successful(Left("Недостаточно средств"))
//        case None => Future.successful(Left("Такой элемент не найден"))
//      }
//
//      acc_to: Future[Account] = find(transferItem.to).map(_.getOrElse(Account(username = "Not found")))
//      acc_from: Future[Account] = find(transferItem.from).map(_.getOrElse(Account(username = "Not found")))
//      res <- Future.sequence(Seq(acc_to, acc_from))
//    } yield res

    for {
      balance <- db.run(AccountTable.filter(_.id === transferItem.from).map(x => x.sum).result.headOption)
      either: Either[String, Account] <- balance match {
        case Some(balance) if balance >= transferItem.amount =>
          db.run {
            AccountTable.filter(_.id === transferItem.from).map(x => x.sum).update(balance - transferItem.amount)
          }
        case Some(balance) if balance < transferItem.amount => Future.successful(Left("Недостаточно средств"))
        case None => Future.successful(Left("Такой элемент не найден"))
      }

      res <- either match {
        case Right(_) => Future.sequence(find(transferItem.from).map(maybeAccount => maybeAccount.map(account => Right(account)).getOrElse(Left("No such account"))),
          replenish(ReplenishItem(transferItem.to, transferItem.amount)) )
        case Left: Left[String, _] => Future.successful(Left)
    }
  } yield res
}

