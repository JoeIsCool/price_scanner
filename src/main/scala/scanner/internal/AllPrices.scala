package scanner.internal

import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE, QUANTITY_TYPE}
import scanner.internal.ItemPrices.QuantityPrice

import scala.collection.immutable.SortedSet

/**
 * Holds all price data. It holds a collection ItemPrices.
 */
private[internal] trait AllPrices {

  /**
   *
   * @param itemCode
   * @return if there are any prices for the itemCode.
   */
  def containsItem(itemCode: ITEM_CODE_TYPE): Boolean

  /**
   * Takes a new price listing. Includes its itemCode, how many of the item is being priced and the price.
   * @param itemCode
   * @param quantity
   * @param price
   * @return A new AllPrices with the new price information.
   */
  def updatedPrice(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE, price: PRICE_TYPE): AllPrices

  /**
   * Remove all prices for an item.
   * @param itemCode
   * @return A new AllPrices without any prices for that item.
   */
  def removedItem(itemCode: ITEM_CODE_TYPE): AllPrices

  /**
   * Removes one price. If it is the only price for the item, then it behaves the same way as removedItem.
   * @param itemCode
   * @param quantity
   * @return A new AllPrices without the price.
   */
  def removedPrice(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE): AllPrices

  /**
   * @param itemCode
   * @return All price data for the given itemCode.
   */
  def getPrices(itemCode: ITEM_CODE_TYPE): Seq[QuantityPrice]

  /**
   *
   * @return All items for which there are prices, but no price for one of an item.
   */
  def missingPriceForOne(): Set[ITEM_CODE_TYPE]
}

private[internal] object AllPrices {

  def apply(priceMap: Map[ITEM_CODE_TYPE, ItemPrices] = Map()): AllPrices = new AllPricesImpl(priceMap)

  class AllPricesImpl(priceMap: Map[ITEM_CODE_TYPE, ItemPrices]) extends AllPrices {

    override def containsItem(itemCode: ITEM_CODE_TYPE) = priceMap.contains(itemCode)

    override def updatedPrice(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE, price: PRICE_TYPE): AllPrices = {
      val oldItemPrices = priceMap.getOrElse(itemCode, ItemPrices(itemCode))
      val updatedItemPrices = oldItemPrices.updated(quantity, price)
      new AllPricesImpl(priceMap.updated(itemCode, updatedItemPrices))
    }

    override def removedItem(itemCode: ITEM_CODE_TYPE): AllPrices = new AllPricesImpl(priceMap.removed(itemCode))

    override def removedPrice(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE): AllPrices =
      priceMap.get(itemCode) match {
        case None => this
        case Some(itemPrices) => {
          val updatedItemPrices = itemPrices.removed(quantity)
          if (updatedItemPrices.isEmpty())
            new AllPricesImpl(priceMap.removed(itemCode))
          else
            new AllPricesImpl(priceMap.updated(itemCode, updatedItemPrices))
        }
      }

    override def getPrices(itemCode: ITEM_CODE_TYPE): Seq[QuantityPrice] =
      priceMap.get(itemCode).map(_.prices).getOrElse(Seq())

    override def missingPriceForOne(): Set[ITEM_CODE_TYPE] =
      priceMap.values.filter(_.missingPriceForOne()).map(_.itemCode()).toSet
  }
}
