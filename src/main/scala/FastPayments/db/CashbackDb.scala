package FastPayments.db
import FastPayments.models.Category
import slick.jdbc.PostgresProfile.api._
import java.util.UUID

object CashbackDb {
  class CategoryTable(tag: Tag) extends Table[Category](tag, "accounts") {
    val id = column[UUID]("id", O.PrimaryKey)
    val name = column[String]("name")
    val percent = column[Float]("percent")

    override def * = (id, name, percent) <> ((Category.apply _).tupled, Category.unapply _)
  }

  val CashbackTable = TableQuery[CategoryTable]
}
