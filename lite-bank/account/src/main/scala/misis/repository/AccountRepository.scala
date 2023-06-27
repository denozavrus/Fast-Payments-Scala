package misis.repository
import misis.model.Account
import scala.concurrent.Future

class AccountRepository(val accountId: Int, val amount: Int) {
    private var accounts: Map[Int, Account] = Map.empty

    def create(accountId: Int, defAmount: Int): Future[Account] = {
        val account = new Account(accountId, defAmount)
        accounts += accountId -> account
        Future.successful(account)
    }
    def get(accountId: Int): Option[Account] = {
        accounts.get(accountId)
    }
    def get_accounts(): List[Account] = {
        accounts.values.toList
    }
    def update(accountId: Int, value: Int): Future[Account] = {
        accounts.get(accountId) match {
            case Some(account) =>
                val updatedAccount = account.update(value)
                accounts += (accountId -> updatedAccount)
                Future.successful(updatedAccount)
            case None => Future.failed(new Exception(s"Аккаунт с ID $accountId не найден"))
        }
    }
}
