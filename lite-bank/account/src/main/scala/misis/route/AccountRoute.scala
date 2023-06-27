package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import misis.repository.AccountRepository
import scala.concurrent.ExecutionContext


class AccountRoute(respository: AccountRepository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

    def routes =
      (path("create" / IntNumber / IntNumber) { (accountId, value) =>
        val account = respository.create(accountId, value)
        complete(account)
      }) ~
        (path("accounts") {
          complete (respository.get_accounts())
        })
}