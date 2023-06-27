package misis.repository
import misis.TopicName
import misis.kafka.Streams
import misis.model.{Account, AccountUpdate, TransferStart}
import io.circe.generic.auto._

class Repository(streams: Streams) {
  implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]

  def transfer(transfer: TransferStart) = {
    if (transfer.value > 0) {
      streams.produceCommand(AccountUpdate(transfer.sourceId, transfer.destinationId, -transfer.value))
    }
  }
}
