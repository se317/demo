import crypto.Exchange.{ExchangeError, ExchangeResponse}
import crypto.Market
import model.{Order, OrderBook, OrderBookEntry}

class MarketSpec extends UnitSpec {
  import MarketSpec._

  "The average price" should "be correctly calculated" in {

    val avgPrice = Market.calcAvgPrice(10000, orderBook.asks)
    assert(avgPrice === 5000)

    val avgPrice2 = Market.calcAvgPrice(13000, orderBook.asks)
    assert(avgPrice2 === 5200)

    val avgPrice3 = Market.calcAvgPrice(7000, orderBook.bids)
    assert(avgPrice3 === 3500)

  }

  "Only valid asset combinations" should "be allowed for processing orders" in {

    val validOrder = Order("coinbase_pro", "BTC", 1000, "USD")
    val response = Market.processOrder(validOrder, orderBook)

    response shouldBe a[ExchangeResponse]

    val validOrder2 = Order("coinbase_pro", "USD", 1000, "BTC")
    val response2 = Market.processOrder(validOrder2, orderBook)

    response2 shouldBe a[ExchangeResponse]

    val invalidOrder = Order("coinbase_pro", "CHF", 1000, "BTC")
    val response3 = Market.processOrder(invalidOrder, orderBook)

    response3 shouldBe a[ExchangeError]

    val validOrder3 = Order("coinbase_pro", "USD", 1000, "ETH")
    val response4 = Market.processOrder(validOrder3, orderBook)

    response4 shouldBe a[ExchangeResponse]

    val validOrder4 = Order("coinbase_pro", "ETH", 1000, "USD")
    val response5 = Market.processOrder(validOrder4, orderBook)

    response5 shouldBe a[ExchangeResponse]

    val validOrder5 = Order("coinbase_pro", "XRP", 1000, "USD")
    val response6 = Market.processOrder(validOrder5, orderBook)

    response6 shouldBe a[ExchangeResponse]

    val validOrder6 = Order("coinbase_pro", "USD", 1000, "XRP")
    val response7 = Market.processOrder(validOrder6, orderBook)

    response7 shouldBe a[ExchangeResponse]

    val validOrder7 = Order("coinbase_pro", "LTC", 1000, "USD")
    val response8 = Market.processOrder(validOrder7, orderBook)

    response8 shouldBe a[ExchangeResponse]

    val validOrder8 = Order("coinbase_pro", "USD", 1000, "LTC")
    val response9 = Market.processOrder(validOrder8, orderBook)

    response9 shouldBe a[ExchangeResponse]

    val validOrder9 = Order("coinbase_pro", "BCH", 1000, "USD")
    val response10 = Market.processOrder(validOrder9, orderBook)

    response10 shouldBe a[ExchangeResponse]

    val validOrder10 = Order("coinbase_pro", "USD", 1000, "BCH")
    val response11 = Market.processOrder(validOrder10, orderBook)

    response11 shouldBe a[ExchangeResponse]

    val validOrder11 = Order("coinbase_pro", "EOS", 1000, "USD")
    val response12 = Market.processOrder(validOrder11, orderBook)

    response12 shouldBe a[ExchangeResponse]

    val validOrder12 = Order("coinbase_pro", "USD", 1000, "EOS")
    val response13 = Market.processOrder(validOrder12, orderBook)

    response13 shouldBe a[ExchangeResponse]

    val invalidOrder2 = Order("coinbase_pro", "EOS", 1000, "EUR")
    val response14 = Market.processOrder(invalidOrder2, orderBook)

    response14 shouldBe a[ExchangeError]
  }
}

object MarketSpec {
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