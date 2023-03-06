package FastPayments.db
import FastPayments.models.CategoryObject
import slick.jdbc.PostgresProfile.api._
import java.util.UUID

object CashbackDb {
  class CategoryTable(tag: Tag) extends Table[CategoryObject](tag, "accounts") {
    val id = column[UUID]("id", O.PrimaryKey)
    val name = column[String]("name")
    val percent = column[Float]("percent")

    override def * = (id, username, sum) <> ((Account.apply _).tupled, Account.unapply _)
  }

  val AccountTable = TableQuery[AccountTable]
}
