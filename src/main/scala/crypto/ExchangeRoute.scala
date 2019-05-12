package crypto

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import model.Order

import scala.util.{Failure, Success}

trait ExchangeRoute extends ExchangeHandler {

  lazy val exchangeRoute: Route =
    path("exchange") {
      post {
        entity(as[Order]) { order ⇒
          parseOrder(order) match {
            case Success(_) ⇒ processOrder(order) { o ⇒
              complete(o)
            }
            case Failure(_) ⇒ complete(StatusCodes.BadRequest)
          }
        }
      }
    }
}

