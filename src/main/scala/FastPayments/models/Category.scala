package FastPayments.models
import java.util.UUID

case class Category(id: UUID = UUID.randomUUID(), name: String, percent: Float)
case class AddCategory(name: String, percent: Float)
case class UpdateCategory(id: UUID, name: Option[String] = None, percent: Option[Float] = None)
