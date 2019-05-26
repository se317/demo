package model

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

case class OrderBook(bids: Seq[OrderBookEntry], asks: Seq[OrderBookEntry])

case class OrderBookEntry(price: BigDecimal, size: BigDecimal, numOrders: Long)

case class OrderBookResponse(sequence: Long, bids: List[(String, String, Int)], asks: List[(String, String, Int)])

object OrderBook {

  def toOrderBook(orderBookResp: OrderBookResponse): Either[String, OrderBook] = {

    def responseToOrderBookEntry(xs: List[(String, String, Int)]) = Try {
      val buff = ListBuffer[OrderBookEntry]()
      xs.foreach { bid ⇒
        val price = BigDecimal(bid._1)
        val size = BigDecimal(bid._2)
        buff += OrderBookEntry(price, size, bid._3)
      }
      buff.toList
    }

    val orderBook = for {
      bids <- responseToOrderBookEntry(orderBookResp.bids)
      asks <- responseToOrderBookEntry(orderBookResp.asks)
    } yield (bids, asks)

    orderBook match {
      case Success(ob) ⇒ Right(OrderBook(ob._1, ob._2))
      case Failure(err) ⇒ Left(err.toString)
    }
  }
}