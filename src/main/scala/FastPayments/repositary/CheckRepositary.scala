package FastPayments.repositary
import java.util.UUID
import FastPayments.models._

trait CheckRepositary {
  def list(): List[Account]
  def get(id: UUID): Account
  def create(item: AddAccount): Account
  def update(item: UpdateAccount): Option[Account]
  def delete(id: UUID): Unit
}
