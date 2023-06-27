package misis.kafka

import akka.actor.ActorSystem
import akka.actor.FSM.Event
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import misis.WithKafka
import akka.stream.scaladsl.Sink
import misis.model.{Account, AccountUpdate, AccountUpdated}

import scala.concurrent.{ExecutionContext, Future}
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.Properties
import scala.jdk.CollectionConverters._
import java.util.Collections

class Streams()(implicit val system: ActorSystem, executionContext: ExecutionContext)
  extends WithKafka {
    override def group: String = "operation"

  kafkaSource[AccountUpdated]
      .filter(event => event.destinationId != -1)
      .map{ e =>
      val command = AccountUpdate(e.destinationId, -1, e.value)
      produceCommand(command)
        println(s"Был проведен перевод на ${e.destinationId} суммы ${e.value}")
    }
    .to(Sink.ignore)
    .run()
}