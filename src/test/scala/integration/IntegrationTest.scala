package integration

import org.scalatest.{FunSpec, Matchers}
import scanner.{InvalidCode, InvalidPriceScannerError, MissingPricesForOne, OrderScanError, OrderScanner, PriceScanner}

class IntegrationTest extends FunSpec with Matchers {

  describe("Price and Order Scanners") {
    describe("total") {
      val priceScanner = PriceScanner()
        .scan(itemCode = "A", quantity = 1, price = 2)
        .scan(itemCode = "A", quantity = 4, price = 7)
        .scan(itemCode = "B", quantity = 1, price = 12)
        .scan(itemCode = "C", quantity = 1, price = 1.25)
        .scan(itemCode = "C", quantity = 6, price = 6)
        .scan(itemCode = "D", quantity = 1, price = .15)

      /**
       * Assumes each character in a string is an itemcode. Get total price for String from priceScanner.
       * @param string
       * @return
       */
      def orderScannerTestHelper(string: String): Option[BigDecimal] = {
        val codes = string.toCharArray.map(_.toString)
        val orderScanner = priceScanner.newOrder().toOption
        val finalOrderScannerEither = codes.foldLeft(orderScanner)((o, s) => o.flatMap(_.scan(s).toOption))
        finalOrderScannerEither.map(_.total())
      }
      it("test multiple") {
        orderScannerTestHelper("ABCDABAA") should equal(Some(BigDecimal(32.40)))
      }
      it("test one item") {
        orderScannerTestHelper("CCCCCCC") should equal(Some(BigDecimal(7.25)))
      }
      it("test one of each") {
        orderScannerTestHelper("ABCD") should equal(Some(BigDecimal(15.40)))
      }
      it("order is irrelevant") {
        val result1 = orderScannerTestHelper("ABCDABCD")
        val result2 = orderScannerTestHelper("DCBADBCA")
        result1 should equal(result2)
      }
      it("nothing is zero") {
        orderScannerTestHelper("") should equal(Some(BigDecimal(0)))
      }
    }
    it("price scanner needs prices for one item") {
      PriceScanner().scan(itemCode = "A", quantity = 6, price = 1).newOrder() should equal(
        Left(MissingPricesForOne(Set("A")))
      )
    }
    it("gives error for scanning non-existent item") {
      val result = PriceScanner().scan(itemCode = "A", quantity = 1, price = 1).newOrder().flatMap(_.scan("B"))
      result should equal(Left(InvalidCode("B")))
    }
    it("rewrites price") {
      val priceOption = PriceScanner()
        .scan(itemCode = "A", quantity = 1, price = 2)
        .scan(itemCode = "A", quantity = 1, price = 1)
        .newOrder()
        .flatMap(_.scan("A"))
        .map(_.total())
        .toOption
      priceOption should equal(Some(BigDecimal(1)))
    }
  }
}
