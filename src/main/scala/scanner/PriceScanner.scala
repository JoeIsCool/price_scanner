package scanner

import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE, QUANTITY_TYPE}

/**
 * Used to scan prices and ultimately create new orders
 */
trait PriceScanner {

  /**
   * Add a price to the prices the price has an itemCode, the quantity of the item being purchased, and the actual price
   * @param itemCode
   * @param quantity
   * @param price
   * @return A new PriceScanner with the added price information.
   */
  def scan(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE, price: PRICE_TYPE): PriceScanner

  /**
   * @return Normally returns an OrderScanner, but if there is bulk price information for an item, but not an
   *         individual price, then InvalidPriceScannerError is returned.
   */
  def newOrder(): Either[InvalidPriceScannerError, OrderScanner]
}
object PriceScanner {
  def apply(): PriceScanner = internal.PriceScanner()
}
