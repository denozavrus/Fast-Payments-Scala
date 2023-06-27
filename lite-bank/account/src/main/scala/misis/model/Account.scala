package misis.model

import java.time.Instant
import java.util.UUID
// Добавить id назначения
case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}
trait Command
case class AccountUpdate(sourceId: Int, destinationId: Int, value: Int)
case class AccountCreate(sourceId: Int, destinationId: Int, value: Int)

trait Event
case class AccountUpdated(
     sourceId: Int,
     destinationId: Int,
     value: Int,
)
