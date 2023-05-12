package misis.repository

import misis.model.Account

import scala.concurrent.Future

class AccountRepository(val accountId: Int, defAmount: Int){
    var account = Account(accountId, defAmount)

    def update(value: Int): Future[Account] = {
        account = account.update(value)
        Future.successful(account)
    }
}
