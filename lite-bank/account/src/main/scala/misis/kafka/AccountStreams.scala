package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountCreate, AccountUpdated}
import misis.repository.AccountRepository

import scala.concurrent.ExecutionContext

class AccountStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
  extends WithKafka {
  def group = s"account-${repository.accountId}"

  kafkaSource[AccountUpdate]
    .filter(command => repository.get(command.sourceId).nonEmpty && repository.get(command.sourceId).get.amount + command.value >= 0)
    .mapAsync(1) { command =>
      repository
        .update(command.sourceId, command.value)
        .map(_ =>
          AccountUpdated(
            sourceId = command.sourceId,
            destinationId = command.destinationId,
            value = command.value
          )
        )
    }
    .to(kafkaSink)
    .run()


  kafkaSource[AccountUpdated]
    .filter(event => repository.get(event.sourceId).nonEmpty && event.destinationId == -1)
    .map { e =>
      println(s"Аккаунт ${e.sourceId} обновлен на сумму ${e.value}. Баланс: ${repository.get(e.sourceId).get.amount}")
      e
    }
    .to(Sink.ignore)
    .run()


  kafkaSource[AccountCreate]
    .filter(command => repository.get(command.sourceId).isEmpty)
    .mapAsync(1) { command =>
      repository
        .create(command.sourceId, command.value)
        .map(_ =>
          AccountUpdated(
            sourceId = command.sourceId,
            destinationId = -1,
            value = command.value
          )
        )
    }
    .to(kafkaSink)
    .run()

}
