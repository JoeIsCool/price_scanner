package scanner.internal

import scanner.Types.{ITEM_CODE_TYPE, PRICE_TYPE, QUANTITY_TYPE}
import scanner.internal.ItemPrices.QuantityPrice
import scanner.internal.Types.{CALCULATE_TOTAL_COST_TYPE, GET_PRICE_FOR_QUANTITY_TYPE}

private[internal] object Types {
  type GET_PRICE_FOR_QUANTITY_TYPE = ((Iterable[QuantityPrice], QUANTITY_TYPE) => PRICE_TYPE)
  type CALCULATE_TOTAL_COST_TYPE = ((AllPrices, Order) => PRICE_TYPE)
}

/**
 * Given price information and an order, calculates the total cost.
 * @param allPrices
 * @param order
 * @return The total cost.
 */
private[internal] object CalculateTotalCost extends CALCULATE_TOTAL_COST_TYPE {

  override def apply(allPrices: AllPrices, order: Order): PRICE_TYPE =
    order.fullOrder
      .map(itemQuantity => {
        val prices = allPrices.getPrices(itemQuantity.itemCode: ITEM_CODE_TYPE)
        CalculatePriceForQuantity(prices, itemQuantity.quantity)
      })
      .sum
}

/**
 * The total cost of one item in an order. This function doesn't need to the the itemCode.
 * It just needs the quantity and the prices at for different quantities.
 *
 * This is a helper function for CalculateTotalCost. Since this function is pretty big by itself, it is separated
 * out for easier testing.
 * @param prices
 * @param quantity
 * @return The total cost.
 */
private[internal] object CalculatePriceForQuantity extends GET_PRICE_FOR_QUANTITY_TYPE {

  def apply(prices: Iterable[QuantityPrice], quantity: QUANTITY_TYPE): PRICE_TYPE =
    prices
      .filter(_.quantity <= quantity)
      .toSeq
      .sortBy(_.quantity)(Ordering[QUANTITY_TYPE].reverse)
      .foldLeft(RunningTotal(quantity))(foldIteration)
      .accumulatedPrice

  sealed case class RunningTotal(remainingItems: QUANTITY_TYPE, accumulatedPrice: PRICE_TYPE = 0)

  private def foldIteration(runningTotal: RunningTotal, price: QuantityPrice): RunningTotal = {
    val numberChargedThisPrice = runningTotal.remainingItems / price.quantity
    val cost = numberChargedThisPrice * price.price
    val accumulatedPrice = runningTotal.accumulatedPrice + cost
    val remainingItems = runningTotal.remainingItems % price.quantity
    RunningTotal(remainingItems, accumulatedPrice)
  }

  def totalPriceForItem(quantity: QUANTITY_TYPE, prices: Seq[QuantityPrice]): PRICE_TYPE =
    prices
      .foldLeft(RunningTotal(quantity))(foldIteration)
      .accumulatedPrice
}
