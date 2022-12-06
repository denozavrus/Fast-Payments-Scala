package FastPayments

import FastPayments.models.{AddItem, UpdateItem}
import repositary.ItemRepositaryMutable

object PaymentsApp extends App {
  val repository = new ItemRepositaryMutable
  val first = repository.add_item(AddItem("111", 300))
  val second = repository.add_item(AddItem("221", 100))
  val third = repository.add_item(AddItem("321", 1000))

  repository.update_item(UpdateItem(first.id, sum = 200))
  repository.delete_item(third.id)

  println(repository.list())
}
