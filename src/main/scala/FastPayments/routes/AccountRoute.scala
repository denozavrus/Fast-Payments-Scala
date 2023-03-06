package FastPayments.routes
import FastPayments.models.{AddAccount, ReplenishItem, TransferItem, UpdateAccount, WithdrawItem}
import FastPayments.repositary.{AccountRepositoryDb, CheckRepositary, CheckRepositaryMutable}
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import spray.json._

class AccountRoute(repository: AccountRepositoryDb) extends FailFastCirceSupport {
  def route: Route =
      (path("accounts") & get) {
        val list = repository.list()
        complete(list)
      } ~
      path("account") {
        (post & entity(as[AddAccount])) {newItem =>
          complete(repository.create(newItem))
        }
      } ~
      path("account" / JavaUUID) { id =>
        get {
          complete(repository.get(id))
        }
      } ~
      path("account") {
        (put & entity(as[UpdateAccount])) { updateItem =>
          complete(repository.update(updateItem))
        }
      } ~
      path("account" / JavaUUID) { id =>
        delete {
          complete(repository.delete(id))
        }
      } ~
      path("replenish"){
        (put & entity(as[ReplenishItem])) { AccInfo =>
          complete(repository.replenish(AccInfo))
        }
      } ~
        path("withdraw") {
          (put & entity(as[WithdrawItem])) { AccInfo =>
            complete(repository.withdraw(AccInfo))
          }
        } ~
        path("transfer") {
          (put & entity(as[TransferItem])) { AccInfo =>
            complete(repository.transfer(AccInfo))
          }
        }
}
