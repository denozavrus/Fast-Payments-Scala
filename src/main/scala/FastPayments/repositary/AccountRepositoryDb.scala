package FastPayments.repositary

import FastPayments.models.{Account, AddAccount, UpdateAccount}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import FastPayments.db.AccountDb._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends CheckRepositary {

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

}

