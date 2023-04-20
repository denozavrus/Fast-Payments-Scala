package FastPayments.repositary
import java.util.UUID
import FastPayments.models._

import scala.concurrent.Future

trait CategoryRepositary {
  def list(): Future[Seq[Category]]

  def get(id: UUID): Future[Category]

  def create(item: AddCategory): Future[Category]

  def update(item: UpdateCategory): Future[Option[Category]]

  def delete(id: UUID): Future[Unit]
}
