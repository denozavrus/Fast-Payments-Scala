package FastPayments.repositary
import FastPayments.models.{Account, AddAccount, UpdateAccount}

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class CheckRepositaryMutable(implicit val ec: ExecutionContext) extends CheckRepositary {
  private val checks = mutable.Map[UUID, Account]()

  override def list(): Future[List[Account]] = Future {
    checks.values.toList
  }

  override def create(add: AddAccount): Future[Account] = Future {
    val item = Account(id = UUID.randomUUID(), username = add.username, sum = add.sum)
    checks.put(item.id, item)
    item
  }

  override def update(update: UpdateAccount): Future[Option[Account]] = Future {
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

  override def get(id: UUID): Future[Account] = Future {
    checks(id)
  }

  override def delete(id: UUID): Future[Unit] = Future {
    checks.remove(id)
  }
}
