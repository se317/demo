package crypto

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{onComplete, provide, reject}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import model.Order

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import Exchange._
import util.ConfigUtil

trait ExchangeHandler {
  implicit val system: ActorSystem = ActorSystem()
  implicit val askTimeout: Timeout = Timeout(30 seconds)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger = Logger("exchangeService")
  private val exchangeService = system.actorOf(Props[Exchange], "exchangeService")

  def processOrder(order: Order): Directive1[ExchangeResponse] = {
    onComplete(exchangeService ? CoinbaseRequest(order)) flatMap {
      case Success(response) ⇒ response match {
        case exr: ExchangeResponse ⇒ provide(exr)
        case err: ExchangeError ⇒
          logger.debug(err.msg)
          reject().toDirective[Tuple1[ExchangeResponse]]
      }
      case Failure(err) ⇒
        logger.debug(s"Error processing order: ${err.getLocalizedMessage}")
        reject().toDirective[Tuple1[ExchangeResponse]]
    }
  }

  def parseOrder(order: Order) = Try {
    require(order.exchange == "coinbase_pro")
    require(order.amount > 0)
    require(ConfigUtil.isValidPair(order))
  }
}
