import crypto.Exchange.{ExchangeError, ExchangeResponse}
import crypto.MarketMaker
import model.{Order, OrderBook, OrderBookEntry}

class MarketMakerSpec extends UnitSpec {
  import MarketMakerSpec._

  "The average price" should "be correctly calculated" in {

    val avgPrice = MarketMaker.calcAvgPrice(10000, orderBook.asks)
    assert(avgPrice === 5000)

    val avgPrice2 = MarketMaker.calcAvgPrice(13000, orderBook.asks)
    assert(avgPrice2 === 5200)

    val avgPrice3 = MarketMaker.calcAvgPrice(7000, orderBook.bids)
    assert(avgPrice3 === 3500)

  }

  "Only valid asset combinations" should "be allowed for processing orders" in {

    val validOrder = Order("coinbase_pro", "BTC", 1000, "USD")
    val response = MarketMaker.processOrder(validOrder, orderBook)

    response shouldBe a[ExchangeResponse]

    val validOrder2 = Order("coinbase_pro", "USD", 1000, "BTC")
    val response2 = MarketMaker.processOrder(validOrder2, orderBook)

    response2 shouldBe a[ExchangeResponse]

    val invalidOrder = Order("coinbase_pro", "CHF", 1000, "BTC")
    val response3 = MarketMaker.processOrder(invalidOrder, orderBook)

    response3 shouldBe a[ExchangeError]

  }
}

object MarketMakerSpec {
  val orderBook = OrderBook(
    bids = List(
      OrderBookEntry(4000, 1, 1),
      OrderBookEntry(3000, 1, 1),
    ),
    asks = List(
      OrderBookEntry(5000, 2, 1),
      OrderBookEntry(6000, 1, 1),
    )
  )
}