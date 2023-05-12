package misis.model

import java.time.Instant
import java.util.UUID

case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountUpdate(accountId: Int, value: Int, category: Option[String], tags: Option[Seq[String]])

trait Event
case class AccountUpdated(
    accountId: Int,
    value: Int,
    publishedAt: Option[Instant] = Some(Instant.now()),
    category: Option[String],
    tags: Option[Seq[String]]
)
