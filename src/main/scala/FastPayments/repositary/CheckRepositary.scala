package FastPayments.repositary
import java.util.UUID
import FastPayments.models._

import scala.concurrent.Future

trait CheckRepositary {
  def list(): Future[Seq[Account]]
  def get(id: UUID): Future[Account]
  def create(item: AddAccount): Future[Account]
  def update(item: UpdateAccount): Future[Option[Account]]
  def delete(id: UUID): Future[Unit]
}
