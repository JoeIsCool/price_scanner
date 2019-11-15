package scanner.internal

import scanner.{InvalidCode, OrderScanError, OrderScanner}
import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE}
import scanner.internal.Types.CALCULATE_TOTAL_COST_TYPE

private[scanner] object OrderScanner {

  def apply(allPrices: AllPrices): OrderScanner = OrderScannerImpl(allPrices)

  sealed case class OrderScannerImpl(allPrices: AllPrices,
                                     order: Order = Order(),
                                     calculateTotalCost: CALCULATE_TOTAL_COST_TYPE = CalculateTotalCost)
      extends OrderScanner {

    override def scan(itemCode: ITEM_CODE_TYPE): Either[OrderScanError, OrderScanner] =
      if (allPrices.containsItem(itemCode))
        Right(OrderScannerImpl(allPrices, order.incrementItem(itemCode)))
      else
        Left(InvalidCode(itemCode))

    override def total(): PRICE_TYPE = calculateTotalCost(allPrices, order)
  }

}
