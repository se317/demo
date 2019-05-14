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

  def buildUrl(order: Order, baseUrl: String): String = {

    val assetCombo = if (order.input == "BTC" || order.output == "BTC") "BTC-USD" else "ETH-USD"
    val builder = StringBuilder.newBuilder
    builder.append(baseUrl)
    builder.append(assetCombo)
    builder.append("/book")
    builder.append("?level=2")

    println(s"url ${builder.toString()}")
    builder.toString

  }

  def fetchOrderBook(url: String): Future[OrderBookResponse] =
    Http().singleRequest(HttpRequest(uri = url)) flatMap {
      response ⇒ Unmarshal(response.entity).to[OrderBookResponse]
    }
}
