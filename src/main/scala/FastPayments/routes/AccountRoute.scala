package FastPayments.routes
import FastPayments.models.{AddAccount, UpdateAccount}
import FastPayments.repositary.{CheckRepositary, CheckRepositaryMutable}
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._


class AccountRoute(repository: CheckRepositary) extends FailFastCirceSupport {
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
      }
}
