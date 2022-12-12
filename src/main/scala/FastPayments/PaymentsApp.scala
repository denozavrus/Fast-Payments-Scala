package FastPayments

import FastPayments.models.{AddAccount, UpdateAccount}
import repositary.CheckRepositaryMutable
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object PaymentsApp extends App {
  val repository = new CheckRepositaryMutable
  val first = repository.create(AddAccount("111", 300))
  val second = repository.create(AddAccount("221", 100))
  val third = repository.create(AddAccount("321", 1000))

  repository.update(UpdateAccount(first.id, sum = 200))
  repository.delete(third.id)

  private val list = repository.list()
  val result = list.asJson.noSpaces
  println(result)
}
