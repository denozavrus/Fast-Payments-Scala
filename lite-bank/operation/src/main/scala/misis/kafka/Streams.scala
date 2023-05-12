package misis.kafka

import akka.actor.ActorSystem
import misis.WithKafka

import scala.concurrent.ExecutionContext

class Streams()(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "operation"
}
