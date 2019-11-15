package scanner

import scanner.Types.ITEM_CODE_TYPE

trait Error

trait OrderScanError extends Error {
  def scanCode: ITEM_CODE_TYPE
}
case class InvalidCode(scanCode: ITEM_CODE_TYPE) extends OrderScanError

trait InvalidPriceScannerError extends Error

case class MissingPricesForOne(codesWithoutIndividualPrices: Set[ITEM_CODE_TYPE]) extends InvalidPriceScannerError
