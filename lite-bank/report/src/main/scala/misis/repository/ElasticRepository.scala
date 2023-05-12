package misis.repository

import com.sksamuel.elastic4s.{ElasticClient, Response}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import io.circe.Json
import misis.model.AccountUpdated
import io.circe.generic._
import io.circe.syntax._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class ElasticRepository(elastic: ElasticClient)(implicit ec: ExecutionContext) {
    def index(event: AccountUpdated): Future[Response[IndexResponse]] = {
        val json = event.asJson.noSpaces
        elastic
            .execute {
                indexInto("account-updated")
                    .doc(json)
            }
            .map { res =>
                println(res)
                res
            }
    }
}
