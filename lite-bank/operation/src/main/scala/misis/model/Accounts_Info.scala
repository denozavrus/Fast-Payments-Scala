package misis.model

import scala.collection.mutable.Map

object Accounts_Info {
  private var accounts: Map[Int, Account] = Map()

  def addAccount(account: Account): Unit = {
    accounts += (account.id -> account)
  }

  def accountExists(accountId: Int): Boolean = {
    accounts.contains(accountId)
  }
}
