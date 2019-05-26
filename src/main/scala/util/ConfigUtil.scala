package util

import java.util.Map.Entry
import scala.collection.JavaConverters._

import com.typesafe.config.{ConfigFactory, ConfigObject, ConfigValue}
import model.Order

object ConfigUtil {
  private val config = ConfigFactory.load

  private lazy val fiat: Map[String, String] = fetchConfigMap("api.coinbase.fiat")
  private lazy val assets: Map[String, String] = fetchConfigMap("api.coinbase.assets")

  private def fetchConfigMap(path: String) = {
    val list: Iterable[ConfigObject] = config.getObjectList(path).asScala
    (for {
      item: ConfigObject <- list
      entry: Entry[String, ConfigValue] <- item.entrySet().asScala
      key = entry.getKey
      value = entry.getValue.unwrapped.toString
    } yield (key, value)).toMap
  }

  private def entry(order: Order, configMap: Map[String, String], f: String ⇒ Boolean): Option[String] = {
    val input = Seq(order.input.toUpperCase, order.output.toUpperCase)
    input.collectFirst { case a if f(a) ⇒ configMap(a) }
  }

  def coinbaseStr(order: Order): Option[String] = entry(order, assets, isValidAsset)

  def isValidPair(order: Order): Boolean =
    entry(order, assets, isValidAsset).isDefined &&
      entry(order, fiat, isValidFiat).isDefined

  def isValidAsset(input: String): Boolean = assets.contains(input.toUpperCase)

  def isValidFiat(input: String): Boolean = fiat.contains(input.toUpperCase)
}