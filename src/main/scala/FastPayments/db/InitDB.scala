package FastPayments.db

import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}
import FastPayments.db.AccountDb.AccountTable
import FastPayments.db.CashbackDb.CashbackTable
import slick.jdbc.PostgresProfile.api._

class InitDB(implicit val ec: ExecutionContext, db: Database) {
  def prepare(): Future[_] = {
    db.run(AccountTable.schema.createIfNotExists)
    db.run(CashbackTable.schema.createIfNotExists)
  }
}
