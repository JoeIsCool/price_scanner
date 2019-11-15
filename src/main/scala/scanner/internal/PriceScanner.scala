package scanner.internal

import scanner.{InvalidPriceScannerError, MissingPricesForOne, OrderScanner}
import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE, QUANTITY_TYPE}

private[scanner] object PriceScanner {

  private val emptyPrintScanner = PriceScannerImpl()

  def apply(): scanner.PriceScanner = emptyPrintScanner

  sealed case class PriceScannerImpl(allPrices: AllPrices = AllPrices()) extends scanner.PriceScanner {

    override def scan(itemCode: ITEM_CODE_TYPE, quantity: QUANTITY_TYPE, price: PRICE_TYPE): scanner.PriceScanner =
      PriceScannerImpl(allPrices.updatedPrice(itemCode, quantity, price))

    override def newOrder(): Either[InvalidPriceScannerError, OrderScanner] = {
      val missingPricesForOne = allPrices.missingPriceForOne()
      if (missingPricesForOne.nonEmpty)
        Left(MissingPricesForOne(missingPricesForOne))
      else
        Right(OrderScanner(allPrices))
    }
  }
}
