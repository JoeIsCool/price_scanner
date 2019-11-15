package scanner.internal

import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE, QUANTITY_TYPE}
import scanner.internal.ItemPrices.QuantityPrice

/**
 * Holds all prices for a given item.
 * It holds the item code and a collection of purchase quantities and the associated prices.
 */
private[internal] trait ItemPrices {

  /**
   *
   * @return the code for this item.
   */
  def itemCode(): ITEM_CODE_TYPE

  /**
   * Add a price for a given quantity
   * @param quantity
   * @param price
   * @return a new ItemPrices with the added Price
   */
  def updated(quantity: QUANTITY_TYPE, price: PRICE_TYPE): ItemPrices

  /**
   * Removes a price
   * @param quantity
   * @return a new ItemPrices with the price removed.
   */
  def removed(quantity: QUANTITY_TYPE): ItemPrices

  /**
   * @return if there are any prices for this item.
   */
  def isEmpty(): Boolean

  /**
   *
   * @return If a price for one of this item has not been set then true, otherwise false.
   */
  def missingPriceForOne(): Boolean

  /**
   *
   * @return All the prices and the corresponding quantities for this item.
   */
  def prices: Seq[QuantityPrice]
}

private[internal] object ItemPrices {

  def apply(itemCode: ITEM_CODE_TYPE): ItemPrices = ItemPricesImpl(itemCode)

  sealed case class QuantityPrice(quantity: QUANTITY_TYPE, price: PRICE_TYPE)

  sealed case class ItemPricesImpl(itemCode: ITEM_CODE_TYPE, priceMap: Map[QUANTITY_TYPE, QuantityPrice] = Map())
      extends ItemPrices {
    override def updated(quantity: QUANTITY_TYPE, price: PRICE_TYPE) =
      ItemPricesImpl(itemCode, priceMap.updated(quantity, QuantityPrice(quantity, price)))

    override def removed(quantity: QUANTITY_TYPE): ItemPrices = ItemPricesImpl(itemCode, priceMap.removed(quantity))

    override def isEmpty(): Boolean = priceMap.isEmpty

    override def missingPriceForOne(): Boolean = !priceMap.contains(1)

    override def prices: Seq[QuantityPrice] = priceMap.values.toSeq
  }
}
