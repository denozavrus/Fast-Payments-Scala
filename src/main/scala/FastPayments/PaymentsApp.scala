package FastPayments
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
import routes.{AccountRoute, HelloRoute}


object PaymentsApp extends App with FailFastCirceSupport {
  implicit val system: ActorSystem = ActorSystem("PaymentsApp")
  implicit val ec = system.dispatcher
  val repository = new CheckRepositaryMutable
  val helloRoute = new HelloRoute().route
  val accountRoute = new AccountRoute(repository).route
  Http().newServerAt("0.0.0.0", port=8081).bind(helloRoute ~ accountRoute)
}
