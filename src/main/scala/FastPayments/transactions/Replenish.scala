package FastPayments.transactions

import FastPayments.models.Account

import java.util.UUID

case class Replenish(account_id: Account, sum: Integer) {
}
