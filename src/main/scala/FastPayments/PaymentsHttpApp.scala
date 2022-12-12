package FastPayments
import FastPayments.PaymentsApp.repository
import FastPayments.models.{AddAccount, UpdateAccount}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import repositary.CheckRepositaryMutable
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object PaymentsHttpApp extends App with FailFastCirceSupport {
  implicit val system: ActorSystem = ActorSystem("PaymentsApp")

  val repository = new CheckRepositaryMutable
  val first = repository.create(AddAccount("111", 300))
  val second = repository.create(AddAccount("221", 100))
  val third = repository.create(AddAccount("321", 1000))

  repository.update(UpdateAccount(first.id, sum = 200))
  repository.delete(third.id)

  val route: Route =
    (path("hello") & get) {
    complete("Hello scala world")
    } ~
    (path("items") & get){
      val list = repository.list()
      complete(list)
    } ~
    path("item") {
      (post & entity(as [AddAccount])) { newItem =>
        complete(repository.create(newItem))
      }
    } ~
    path("item" / JavaUUID ) { id =>
        get {
          complete(repository.get(id))
        }
    } ~
    path ("item") {
      (put & entity(as[UpdateAccount])) { updateItem =>
        complete(repository.update(updateItem))
      }
    } ~
    path("item" / JavaUUID) { id =>
      delete {
        complete(repository.delete(id))
      }
    }

  Http().newServerAt("0.0.0.0", port=8081).bind(route)
}
