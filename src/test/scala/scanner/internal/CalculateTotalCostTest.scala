package scanner.internal

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers}
import scanner.internal.ItemPrices.QuantityPrice
import scanner.internal.Order.ItemQuantity

class CalculateTotalCostTest extends FunSpec with Matchers with MockFactory {
  describe("CalculatePriceForQuantity") {
    it("calculates correctly for one price") {
      val prices = Seq(QuantityPrice(quantity = 1, price = 5))
      val result = CalculatePriceForQuantity(prices, 3)
      result should equal(BigDecimal(15))
    }
    it("calculates correctly for two prices") {
      val prices = Seq(QuantityPrice(quantity = 1, price = 5), QuantityPrice(quantity = 2, price = 7))
      val result = CalculatePriceForQuantity(prices, 5)
      result should equal(BigDecimal(19))
    }
    it("calculates correctly for many prices") {
      val prices = Seq(QuantityPrice(quantity = 1, price = 5),
                       QuantityPrice(quantity = 3, price = 7),
                       QuantityPrice(quantity = 6, price = 11),
                       QuantityPrice(quantity = 19, price = 31))
      val result = CalculatePriceForQuantity(prices, 36)
      result should equal(BigDecimal(70))
    }
  }

  it("CalculateTotalCost") {
    val allPrices = mock[AllPrices]
    val order = mock[Order]
    (order.fullOrder _)
      .expects()
      .returning(Seq(ItemQuantity("A", 4), ItemQuantity("B", 2), ItemQuantity("C", 1), ItemQuantity("D", 1)))
    (allPrices.getPrices _)
      .expects("A")
      .returning(Seq(QuantityPrice(quantity = 1, price = 2), QuantityPrice(quantity = 4, price = 7)))
    (allPrices.getPrices _).expects("B").returning(Seq(QuantityPrice(quantity = 1, price = 12)))
    (allPrices.getPrices _)
      .expects("C")
      .returning(Seq(QuantityPrice(quantity = 1, price = 1.25), QuantityPrice(quantity = 6, price = 6)))
    (allPrices.getPrices _).expects("D").returning(Seq(QuantityPrice(quantity = 1, price = .15)))
    val result = CalculateTotalCost(allPrices, order)
    result should equal(BigDecimal(32.40))
  }
}
