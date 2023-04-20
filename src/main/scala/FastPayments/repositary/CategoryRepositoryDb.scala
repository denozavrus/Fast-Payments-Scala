package FastPayments.repositary

import FastPayments.models.{AddCategory, Category, UpdateCategory}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import FastPayments.db.CashbackDb._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class CategoryRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends CategoryRepositary {

  override def list(): Future[Seq[Category]] = {
    db.run(CashbackTable.result)
  }

  override def create(add: AddCategory): Future[Category] = {
    val item = Category(name = add.name, percent = add.percent)
    for {
      _ <- db.run(CashbackTable += item)
      res <- get(item.id)
    } yield res
  }

  override def get(id: UUID): Future[Category] = {
    db.run(CashbackTable.filter(_.id === id).result.head)
  }

  override def delete(id: UUID): Future[Unit] = Future {
    db.run(CashbackTable.filter(_.id === id).delete).map(_ => ())
  }

  def find(id: UUID): Future[Option[Category]] = {
    db.run(CashbackTable.filter(_.id === id).result.headOption)
  }

  override def update(item: UpdateCategory): Future[Option[Category]] = {
    for {
      _ <- db.run{
        CashbackTable.filter(_.id === item.id)
          .map(a => (a.name, a.percent))
          .update((item.name getOrElse "None", item.percent getOrElse 0))
      }
      res <- find(item.id)
    } yield res
  }
}
