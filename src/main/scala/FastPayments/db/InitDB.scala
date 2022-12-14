package FastPayments.db

import slick.jdbc.JdbcBackend.Database
import scala.concurrent.{ExecutionContext, Future}
import FastPayments.db.AccountDb.AccountTable
import slick.jdbc.PostgresProfile.api._

class InitDB(implicit val ec: ExecutionContext, db: Database) {
  def prepare(): Future[_] = {
    db.run(AccountTable.schema.createIfNotExists)
  }
}
