package misis.route

import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.Streams
import misis.model.{AccountUpdate, TransferStart, AccountCreate}
import misis.repository.Repository
import scala.concurrent.ExecutionContext


class Route(streams: Streams, repository: Repository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

    implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]
    implicit val createTopicName: TopicName[AccountCreate] = streams.simpleTopicName[AccountCreate]

    def routes =
        (path("hello") & get) {
            complete("ok")
        } ~
            (path("update" / IntNumber / Segment) { (accountId, value) =>
                val command = AccountUpdate(accountId, -1, value.toInt)
                streams.produceCommand(command)
                complete(command)
            }) ~
            (path("transfer") & post & entity(as[TransferStart])) { transfer =>
                repository.transfer(transfer)
                complete(transfer)
            } ~
          (path("create") & post & entity(as[AccountCreate])) { create =>
            val command = AccountCreate(create.sourceId, -1, create.value)
            streams.produceCommand(command)
            complete(command)
          }
}


