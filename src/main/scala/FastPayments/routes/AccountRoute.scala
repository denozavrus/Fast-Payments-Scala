package FastPayments.routes
import FastPayments.models.{AddAccount, AddCategory, ReplenishItem, TransferItem, UpdateAccount, UpdateCategory, WithdrawItem}
import FastPayments.repositary.{AccountRepositoryDb, CategoryRepositoryDb}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._


class AccountRoute(acc_repository: AccountRepositoryDb, cat_repository: CategoryRepositoryDb) extends FailFastCirceSupport {
  def route: Route =
      (path("accounts") & get) {
        val list = acc_repository.list()
        complete(list)
      } ~
      path("account") {
        (post & entity(as[AddAccount])) {newItem =>
          complete(acc_repository.create(newItem))
        }
      } ~
      path("account" / JavaUUID) { id =>
        get {
          complete(acc_repository.get(id))
        }
      } ~
      path("account") {
        (put & entity(as[UpdateAccount])) { updateItem =>
          complete(acc_repository.update(updateItem))
        }
      } ~
      path("account" / JavaUUID) { id =>
        delete {
          complete(acc_repository.delete(id))
        }
      } ~
      path("replenish"){
        (put & entity(as[ReplenishItem])) { AccInfo =>
          complete(acc_repository.replenish(AccInfo))
        }
      } ~
        path("withdraw") {
          (put & entity(as[WithdrawItem])) { AccInfo =>
            complete(acc_repository.withdraw(AccInfo))
          }
        } ~
        path("transfer") {
          (put & entity(as[TransferItem])) { AccInfo =>
            complete(acc_repository.transfer(AccInfo))
          }
        } ~
        (path("categories") & get) {
          val list = cat_repository.list()
          complete(list)
        } ~
        path("category" / JavaUUID) { id =>
          get {
            complete(cat_repository.get(id))
          }
        } ~
        path("category") {
          (post & entity(as[AddCategory])) { newItem =>
            complete(cat_repository.create(newItem))
          }
        } ~
        path("category") {
          (put & entity(as[UpdateCategory])) { updateItem =>
            complete(cat_repository.update(updateItem))
          }
        } ~
        path("category" / JavaUUID) { id =>
          delete {
            complete(cat_repository.delete(id))
          }
        }
}
