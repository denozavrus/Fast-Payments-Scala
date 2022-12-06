package FastPayments.repositary
import java.util.UUID
import FastPayments.models._

trait ItemRepositary {
  def list(): List[Item]
  def add_item(item: AddItem): Item
  def update_item(item: UpdateItem): Option[Item]
  def delete_item(id: UUID): Unit
}
