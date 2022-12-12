package FastPayments.routes

import akka.http.scaladsl.server.Directives.{complete, path}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import akka.http.scaladsl.server.Directives._


class HelloRoute extends FailFastCirceSupport {
  def route: Route =
    (path("hello") & get) {
      complete("Hello scala world")
    }
}
