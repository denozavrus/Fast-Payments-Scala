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
    for
    {
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

    db.run (updateQuery)

    find(update.id)
  }

  override def get(id: UUID): Future[Account] = {
    db.run(AccountTable.filter(_.id === id).result.head)
  }
  override def delete(id: UUID): Future[Unit] = Future {
    db.run(AccountTable.filter(_.id === id).delete).map(_ => ())
  }

  override def Replenish(replenishItem: ReplenishItem): Future[Option[Account]] = {
    for {
        balance <- db.run (AccountTable.filter(_.id === replenishItem.id).map(x => x.sum).result.headOption)
        _ = balance.map{balance =>
          db.run {
            AccountTable.filter(_.id === replenishItem.id).map(x => x.sum).update(balance + replenishItem.amount)
          }
        }.getOrElse("Такой элемент не найден")

        res <- find(replenishItem.id)
    } yield res
  }

  override def Withdraw(withdrawItem: WithdrawItem): Future[Option[Account]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === withdrawItem.id).map(x => x.sum).result.headOption)
      _ = balance.map{
        case balance if balance >= withdrawItem.amount =>
          db.run {
            AccountTable.filter(_.id === withdrawItem.id).map(x => x.sum).update(balance - withdrawItem.amount)
          }
        case balance if balance < withdrawItem.amount => "Недостаточно средств"

      }.getOrElse("Такой элемент не найден")

      res <- find(withdrawItem.id)
    } yield res
  }

  override def Transfer(transferItem: TransferItem): Future[Seq[Account]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === transferItem.from).map(x => x.sum).result.headOption)
      status = balance.map {
        case balance if balance >= transferItem.amount =>
          db.run {
            AccountTable.filter(_.id === transferItem.from).map(x => x.sum).update(balance - transferItem.amount)
          }
        case balance if balance < transferItem.amount => "Недостаточно средств"
      }.getOrElse("Такой элемент не найден")

      _ = status match {
        case _: String => None
        case _ => Replenish(ReplenishItem(transferItem.to, transferItem.amount))
      }
      acc_to: Future[Account] = find(transferItem.to).map(_.getOrElse(Account(username = "Not found")))
      acc_from: Future[Account] = find(transferItem.from).map(_.getOrElse(Account(username = "Not found")))
      res <- Future.sequence(Seq(acc_to, acc_from))
    } yield res
  }
}

