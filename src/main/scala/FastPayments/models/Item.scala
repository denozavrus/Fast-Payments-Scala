package FastPayments.models
import java.util.UUID

case class Item(id: UUID, short_number: String, sum: Int = 0)
case class AddItem(short_number: String, sum: Int)
case class UpdateItem(id: UUID, short_number: String = "", sum: Int)