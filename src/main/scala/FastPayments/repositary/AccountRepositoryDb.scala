package FastPayments.repositary

import FastPayments.models.{Account, AddAccount, UpdateAccount}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import FastPayments.db.AccountDb._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends CheckRepositary with Transactions {

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
    for {
      _ <- db.run {
        AccountTable.filter(_.id === update.id).map(x => (x.username, x.sum)).update((update.username.toString, update.sum))
      }
      res <- find(update.id)
    } yield res
  }

  override def get(id: UUID): Future[Account] = {
    db.run(AccountTable.filter(_.id === id).result.head)
  }
  override def delete(id: UUID): Future[Unit] = Future {
    db.run(AccountTable.filter(_.id === id).delete).map(_ => ())
  }

  override def Replenish(id: UUID, amount: Int): Future[Option[Account]] = {
    for {
        balance <- db.run (AccountTable.filter(_.id === id).map(x => x.sum).result.headOption)
        _ = balance.map{balance =>
          db.run {
            AccountTable.filter(_.id === id).map(x => x.sum).update(balance + amount)
          }
        }.getOrElse("Такой элемент не найден")

        res <- find(id)
    } yield res
  }

  override def Withdraw(id: UUID, amount: Int): Future[Option[Account]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === id).map(x => x.sum).result.headOption)
      _ = balance.map{
        case balance if balance >= amount =>
          db.run {
            AccountTable.filter(_.id === id).map(x => x.sum).update(balance - amount)
          }
        case balance if balance < amount => "Недостаточно средств"

      }.getOrElse("Такой элемент не найден")

      res <- find(id)
    } yield res
  }

  override def Transfer(from: UUID, to: UUID, amount: Int): Future[Seq[Future[Option[Account]]]] = {
    for {
      balance <- db.run(AccountTable.filter(_.id === from).map(x => x.sum).result.headOption)
      status = balance.map {
        case balance if balance >= amount =>
          db.run {
            AccountTable.filter(_.id === from).map(x => x.sum).update(balance - amount)
          }
        case balance if balance < amount => "Недостаточно средств"
      }.getOrElse("Такой элемент не найден")

      _ = status match {
        case _: String => None
        case _ => Replenish(to, amount)
      }
      res <- Future(Seq(find(to), find(from)))
    } yield res
  }
}

