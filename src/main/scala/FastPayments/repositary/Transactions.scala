package FastPayments.repositary

import FastPayments.models.{Account, UpdateAccount}
import jdk.jfr.DataAmount
import slick.jdbc.JdbcBackend.Database

import java.util.UUID
import scala.concurrent.Future

trait Transactions {
  def Replenish(id: UUID, amount: Int): Future[Option[Account]]
  def Withdraw(id: UUID, amount: Int): Future[Option[Account]]
  def Transfer(from: UUID, to: UUID, amount: Int): Future[Seq[Future[Option[Account]]]]
}
