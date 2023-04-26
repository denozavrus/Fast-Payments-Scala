package FastPayments.models
import java.util.UUID

case class Account(id: UUID = UUID.randomUUID(), username: String, sum: Float = 0)
case class AddAccount(username: String, sum: Float)
case class UpdateAccount(id: UUID, username: Option[String] = None, sum: Option[Float] = None)