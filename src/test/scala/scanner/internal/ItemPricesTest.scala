package scanner.internal

import org.scalatest.{FunSpec, Matchers}
import scanner.internal.ItemPrices.QuantityPrice

class ItemPricesTest extends FunSpec with Matchers {
  val initialItemPrices = ItemPrices("A")
  describe("ItemPrices") {

    describe("isEmpty") {
      it("returns true") {
        initialItemPrices.isEmpty() should equal(true)
      }
      it("reports false") {
        initialItemPrices.updated(5, 6).isEmpty() should equal(false)
      }
    }

    describe("updated") {
      it("one price") {
        val prices = initialItemPrices.updated(5, 6).prices
        prices should contain.theSameElementsInOrderAs(Seq(QuantityPrice(5, 6)))
      }
      it("two prices") {
        val prices = initialItemPrices.updated(5, 6).updated(7, 8).prices
        prices should contain.theSameElementsInOrderAs(Seq(QuantityPrice(5, 6), QuantityPrice(7, 8)))
      }
    }

    describe("missingPriceForOne") {
      it("true") {
        initialItemPrices.updated(5, 6).missingPriceForOne() should equal(true)
      }
      it("false") {
        initialItemPrices.updated(1, 6).missingPriceForOne() should equal(false)
      }
    }

    it("removed") {
      val prices = initialItemPrices.updated(5, 6).updated(7, 8).removed(7).prices
      prices should contain.theSameElementsInOrderAs(Seq(QuantityPrice(5, 6)))
    }
  }
}
