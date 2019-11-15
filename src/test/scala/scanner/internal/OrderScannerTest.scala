package scanner.internal

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers}
import scanner.InvalidCode
import scanner.internal.OrderScanner.OrderScannerImpl
import scanner.internal.Types.CALCULATE_TOTAL_COST_TYPE

class OrderScannerTest extends FunSpec with Matchers with MockFactory {
  describe("OrderScanner") {
    val allPrices = mock[AllPrices]
    val order = mock[Order]
    val calculateTotalCost = mock[CALCULATE_TOTAL_COST_TYPE]
    val secondOrder = mock[Order]
    val orderScanner = OrderScannerImpl(allPrices, order, calculateTotalCost)
    describe("scan") {
      it("when containsItem returns false") {
        (allPrices.containsItem _).expects("A").returning(false)
        orderScanner.scan("A") should equal(Left(InvalidCode("A")))
      }
      it("when containsItem returns true") {
        (allPrices.containsItem _).expects("A").returning(true)
        (order.incrementItem _).expects("A").returning(secondOrder)
        orderScanner.scan("A") match {
          case Left(_) => fail("returned error when good result expected")
          case Right(OrderScannerImpl(allPricesReturned, orderReturned, calculateTotalCost)) => {
            allPricesReturned should equal(allPrices)
            orderReturned should equal(secondOrder)
            calculateTotalCost should equal(calculateTotalCost)
          }
          case _ => fail("unknown error")
        }
      }
    }

    it("total") {
      (calculateTotalCost.apply _).expects(allPrices, order).returning(BigDecimal(6))
      orderScanner.total() should equal(BigDecimal(6))
    }
  }
}
