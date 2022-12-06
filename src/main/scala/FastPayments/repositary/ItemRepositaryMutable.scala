package FastPayments.repositary
import FastPayments.models.{AddItem, Item, UpdateItem}

import java.util.UUID
import scala.collection.mutable

class ItemRepositaryMutable extends ItemRepositary {
  private val checks = mutable.Map[UUID, Item]()

  override def list(): scala.List[Item] = {
    checks.values.toList
  }

  override def add_item(add: AddItem): Item = {
    val item = Item(id = UUID.randomUUID(), short_number = add.short_number, sum = add.sum)
    checks.put(item.id, item)
    item
  }

  override def update_item(update: UpdateItem): Option[Item] = {
    checks.get(update.id).map { item =>
      val updated = item.copy(short_number =
        if (update.short_number == "") item.short_number
        else update.short_number,
        sum = update.sum)
      checks.put(item.id, updated)
      updated
    }
  }

  override def delete_item(id: UUID): Unit = {
    checks.remove(id)
  }
}
