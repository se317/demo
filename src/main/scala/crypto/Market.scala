package crypto

import java.time.{LocalDateTime, ZoneOffset}

import com.typesafe.scalalogging.Logger
import crypto.Exchange.{ExchangeCmd, ExchangeError, ExchangeResponse}
import model.{Order, OrderBook, OrderBookEntry}
import util.ConfigUtil

import scala.annotation.tailrec

object Market {
  val logger = Logger("Market")

  /**
    * calculates average price for stated currency amount. Orders that can not be fully met get partially executed.
    * @param amount fiat currency amount
    * @param xs asks or bids from order book
    * @return average price of asset based on order book entries
    */
  def calcAvgPrice(amount: BigDecimal, xs: Seq[OrderBookEntry]): BigDecimal = {
    @tailrec
    def loop(amount: BigDecimal, xs: Seq[OrderBookEntry], acc: BigDecimal): BigDecimal = {
      xs match {
        case Nil ⇒ acc
        case head :: tail ⇒
          val vol = head.price * head.size
          if (vol < amount) {
            logger.debug(s" volume: $amount - $vol = ${amount - vol}, accumulated: $acc = ${head.size} + ${acc + head.size}")
            loop(amount - vol, tail, acc + head.size)
          }
          else {
            logger.debug(s" $amount / ${head.price} = ${amount/head.price} + $acc = ${amount/head.price + acc}")
            amount / head.price + acc
          }
      }
    }

    val conversion = loop(amount, xs, 0.0)
    val avgPrice = amount / conversion
    logger.debug(s" AvgPrice: $amount / $conversion = $avgPrice")

    avgPrice
  }

  /**
    * processes order based on supported crypto currency / fiat currency permutations
    * @param order order
    * @param orderBook orderBook
    * @return `ExchangeResponse` or `ExchangeError`
    */
  def processOrder(order: Order, orderBook: OrderBook): ExchangeCmd = {
    if(ConfigUtil.isValidAsset(order.input) && ConfigUtil.isValidFiat(order.output)){
      val avgPrice = calcAvgPrice(order.amount, orderBook.asks)
      ExchangeResponse(avgPrice, LocalDateTime.now(ZoneOffset.UTC))
    } else if(ConfigUtil.isValidFiat(order.input) && ConfigUtil.isValidAsset(order.output)) {
      val avgPrice = calcAvgPrice(order.amount, orderBook.bids)
      ExchangeResponse(avgPrice, LocalDateTime.now(ZoneOffset.UTC))
    }
    else
      ExchangeError("invalid asset combination")
  }
}