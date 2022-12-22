package FastPayments.repositary

import FastPayments.models.{Account, ReplenishItem, TransferItem, UpdateAccount, WithdrawItem}
import jdk.jfr.DataAmount
import slick.jdbc.JdbcBackend.Database

import java.util.UUID
import scala.concurrent.Future

trait TransactionTypes {
  def Replenish(replenishItem: ReplenishItem): Future[Option[Account]]
  def Withdraw(withdrawItem: WithdrawItem): Future[Option[Account]]
  def Transfer(transferItem: TransferItem): Future[Seq[Account]]
}
