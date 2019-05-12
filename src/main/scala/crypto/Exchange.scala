package crypto

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.pattern.pipe
import api.coinbase.CoinbasePro
import com.typesafe.config.ConfigFactory
import crypto.Exchange.{CoinbaseRequest, CoinbaseResponse, ExchangeError}
import model._

import scala.concurrent.ExecutionContextExecutor

class Exchange extends Actor with ActorLogging {

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val config = ConfigFactory.load
  private val baseUrl = config.getString("api.coinbase.url")

  override def receive: Receive = {

    case CoinbaseRequest(order) ⇒
      val url = CoinbasePro.buildUrl(baseUrl)
      CoinbasePro.fetchOrderBook(url)
        .map(CoinbaseResponse.apply(order,_))
        .pipeTo(self)(sender())

    case CoinbaseResponse(order, resp) ⇒
      OrderBook.toOrderBook(resp) match {
        case Right(ob) ⇒ sender() ! MarketMaker.processOrder(order, ob)
        case Left(err) ⇒ sender() ! ExchangeError(s"Can not parse Coinbase Orderbook: $err")
      }
  }

}
object Exchange {
  sealed trait ExchangeCmd

  final case class CoinbaseRequest(order: Order) extends ExchangeCmd
  final case class CoinbaseResponse(order: Order, orderBookResp: OrderBookResponse) extends ExchangeCmd

  final case class ExchangeResponse(avgPrice: BigDecimal, timestamp: LocalDateTime) extends ExchangeCmd
  final case class ExchangeError(msg: String) extends ExchangeCmd
}
