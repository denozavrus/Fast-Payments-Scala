package FastPayments.models
import java.util.UUID

case class Account(id: UUID, username: String, sum: Int = 0)

case class AddAccount(username: String, sum: Int)

case class UpdateAccount(id: UUID, username: Option[String] = None, sum: Int)