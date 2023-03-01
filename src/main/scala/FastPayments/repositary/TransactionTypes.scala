package FastPayments.repositary

import FastPayments.models.{Account, ReplenishItem, TransferItem, UpdateAccount, WithdrawItem}
import jdk.jfr.DataAmount
import slick.jdbc.JdbcBackend.Database

import java.util.UUID
import scala.concurrent.Future

trait TransactionTypes {
  def Replenish(replenishItem: ReplenishItem): Future[Either[String, Account]]
  def Withdraw(withdrawItem: WithdrawItem): Future[Either[String, Account]]
  def Transfer(transferItem: TransferItem): Future[Either[String, Seq[Account]]]
}
