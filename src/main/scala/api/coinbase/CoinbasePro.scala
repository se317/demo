package api.coinbase

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import model._
import util.ConfigUtil

import scala.concurrent.{ExecutionContextExecutor, Future}

object CoinbasePro extends ErrorAccumulatingCirceSupport {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val config = ConfigFactory.load
  private val baseUrl = config.getString("api.coinbase.url")

  def buildUrl(order: Order): Either[String, String] = {
    ConfigUtil.coinbaseStr(order) match {
      case Some(value) ⇒
        val builder = StringBuilder.newBuilder
        builder.append(baseUrl)
        builder.append(value)
        builder.append("/book")
        builder.append("?level=2")
        Right(builder.toString)

      case None ⇒ Left("Invalid coinbase asset request")
    }
  }

  def fetchOrderBook(url: String): Future[OrderBookResponse] =
    Http().singleRequest(HttpRequest(uri = url)) flatMap {
      response ⇒ Unmarshal(response.entity).to[OrderBookResponse]
    }
}
