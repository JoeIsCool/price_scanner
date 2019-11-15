package scanner.internal

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers}
import scanner.MissingPricesForOne
import scanner.internal.OrderScanner.OrderScannerImpl
import scanner.internal.PriceScanner.PriceScannerImpl

class PriceScannerTest extends FunSpec with Matchers with MockFactory {
  describe("PriceScanner") {
    val allPrices = mock[AllPrices]
    val allPrices2 = mock[AllPrices]
    val printScanner = PriceScannerImpl(allPrices)
    it("scans") {
      (allPrices.updatedPrice _).expects("A", 1, BigDecimal(1)).returning(allPrices2)
      printScanner.scan("A", 1, BigDecimal(1)) match {
        case PriceScannerImpl(returnedAllPrices) => returnedAllPrices should equal(allPrices2)
        case _                                   => fail("Print scan returned a bad result")
      }
    }
    describe("new Order") {
      it("should return error") {
        (allPrices.missingPriceForOne _).expects().returning(Set("A"))
        printScanner.newOrder() match {
          case Left(MissingPricesForOne(s)) => s should equal(Set("A"))
          case _                            => fail("should have returned missing")
        }
      }
      it("should return scanner") {
        (allPrices.missingPriceForOne _).expects().returning(Set())
        printScanner.newOrder() match {
          case Right(OrderScannerImpl(returnedAllPrices, _, _)) => returnedAllPrices should equal(allPrices)
          case _                                                => fail("should have returned scanner")
        }
      }

    }
  }

}
