package FastPayments
import FastPayments.db.InitDB
import FastPayments.routes.{AccountRoute, HelloRoute}
import repositary.AccountRepositoryDb
import repositary.CategoryRepositoryDb
import akka.actor.ActorSystem
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import slick.jdbc.JdbcBackend.Database
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import slick.jdbc.PostgresProfile.api._


object PaymentsDbApp extends App with FailFastCirceSupport {
  implicit val system: ActorSystem = ActorSystem("PaymentsApp")
  implicit val ec = system.dispatcher
  implicit val db = Database.forConfig("database.postgres")

  new InitDB().prepare()
  val accRepository = new AccountRepositoryDb
  val catRepository = new CategoryRepositoryDb
  val helloRoute = new HelloRoute().route
  val accountRoute = new AccountRoute(accRepository, catRepository).route
  Http().newServerAt("0.0.0.0", port = 8081).bind(helloRoute ~ accountRoute)

}
