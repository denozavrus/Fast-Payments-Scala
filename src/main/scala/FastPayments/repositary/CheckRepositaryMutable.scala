package FastPayments.repositary
import FastPayments.models.{AddAccount, Account, UpdateAccount}

import java.util.UUID
import scala.collection.mutable

class CheckRepositaryMutable extends CheckRepositary {
  private val checks = mutable.Map[UUID, Account]()

  override def list(): scala.List[Account] = {
    checks.values.toList
  }

  override def create(add: AddAccount): Account = {
    val item = Account(id = UUID.randomUUID(), username = add.username, sum = add.sum)
    checks.put(item.id, item)
    item
  }

  override def update(update: UpdateAccount): Option[Account] = {
    checks.get(update.id).map { item =>
      val updated = item.copy(username =
        update.username match
        {
          case Some(name) => update.username.toString
          case None => item.username
        },
        sum = update.sum)
      checks.put(item.id, updated)
      updated
    }
  }

  override def get(id: UUID): Account = {
    checks(id)
  }

  override def delete(id: UUID): Unit = {
    checks.remove(id)
  }
}
