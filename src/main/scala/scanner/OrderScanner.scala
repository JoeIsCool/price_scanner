package scanner

import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE}

/**
 * Scan items for order and then calculate the total cost
 */
trait OrderScanner {

  /**
   * Adds an item to the order by its itemCode. The total number of times a given item is scanned is collected.
   * @param code
   * @return If the there is no price information for the item code then an error is returned.
   *         Otherwise, a new OrderScanner is returned which reflects the addition to the order.
   */
  def scan(code: ITEM_CODE_TYPE): Either[OrderScanError, OrderScanner]

  /**
   * @return The total price of the order
   */
  def total(): PRICE_TYPE
}
