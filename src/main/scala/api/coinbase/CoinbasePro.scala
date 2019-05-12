package api.coinbase

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import model._

import scala.concurrent.{ExecutionContextExecutor, Future}

object CoinbasePro extends ErrorAccumulatingCirceSupport {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def buildUrl(baseUrl: String): String = {
    val builder = StringBuilder.newBuilder
    builder.append(baseUrl)
    builder.append("BTC")
    builder.append("-")
    builder.append("USD")
    builder.append("/book")
    builder.append("?level=2")

    builder.toString
  }

  def fetchOrderBook(url: String): Future[OrderBookResponse] =
    Http().singleRequest(HttpRequest(uri = url)) flatMap {
      response ⇒ Unmarshal(response.entity).to[OrderBookResponse]
    }
}
