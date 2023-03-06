package FastPayments.repositary

import FastPayments.models.{Account, ReplenishItem, TransferItem, TransferResponse, UpdateAccount, WithdrawItem}
import jdk.jfr.DataAmount
import slick.jdbc.JdbcBackend.Database

import java.util.UUID
import scala.concurrent.Future

trait TransactionTypes {
  def replenish(replenishItem: ReplenishItem): Future[Either[String, Account]]
  def withdraw(withdrawItem: WithdrawItem): Future[Either[String, Account]]
  def transfer(transferItem: TransferItem): Future[Either[String, TransferResponse]]
}
