package crypto

import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.language.postfixOps

object Server extends App with ExchangeRoute {
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(5 seconds)

  private val config = ConfigFactory.load
  private val host = config.getString("http.host")
  private val port = config.getInt("http.port")

  lazy val routes = exchangeRoute

  Http().bindAndHandle(routes, host, port) map { binding ⇒
    logger.info(s"ExchangeServer interface bound to ${binding.localAddress}") } recover {
    case ex ⇒ logger.info(s"ExchangeServer interface could not bind to $host:$port, ${ex.getMessage}")
  }
}
