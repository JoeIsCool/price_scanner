package scanner.internal

import scanner.Types.{ITEM_CODE_TYPE, QUANTITY_TYPE}
import scanner.internal.Order.ItemQuantity

/**
 * Holds data for an order. It basically holds a collection of itemCodes with the quantity of that item in the order.
 */
private[internal] trait Order {

  /**
   * Adds one more of a given item to the order
   * @param itemCode
   * @return A new Order with the item added
   */
  def incrementItem(itemCode: ITEM_CODE_TYPE): Order

  /**
   *
   * @return All the items in the order and the how many of that item there are.
   */
  def fullOrder: Seq[ItemQuantity]
}
private[internal] object Order {

  def apply(): Order = new OrderImpl()

  sealed case class ItemQuantity(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE)

  case class OrderImpl(orderMap: Map[ITEM_CODE_TYPE, ItemQuantity] = Map()) extends Order {

    override def incrementItem(itemCode: ITEM_CODE_TYPE): Order = {
      val oldQuantity: QUANTITY_TYPE = orderMap
        .get(itemCode)
        .map(_.quantity)
        .getOrElse(0)
      val quantity: QUANTITY_TYPE = oldQuantity + 1
      val updatedValue = ItemQuantity(itemCode, quantity)
      val updatedMap = orderMap.updated(itemCode, updatedValue)
      OrderImpl(updatedMap)
    }

    override def fullOrder(): Seq[ItemQuantity] = orderMap.values.toSeq
  }
}
