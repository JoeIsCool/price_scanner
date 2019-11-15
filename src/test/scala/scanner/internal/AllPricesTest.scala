package scanner.internal

import org.scalatest.{FunSpec, Matchers}
import scanner.internal.ItemPrices.QuantityPrice

class AllPricesTest extends FunSpec with Matchers {
  describe("AllPrices") {

    describe("contains item") {
      it("returns false") {
        AllPrices().containsItem("A") should equal(false)
      }
      it("returns true") {
        val prices = AllPrices().updatedPrice("A", 5, 5).getPrices("A")
        prices should contain.theSameElementsInOrderAs(Seq(QuantityPrice(5, 5)))
      }
    }

    describe("updatedPrice") {
      it("adds two items") {
        val result = AllPrices()
          .updatedPrice("A", 5, 5)
          .updatedPrice("B", 1, 1)
          .updatedPrice("A", 6, 6)
        result.getPrices("A") should contain.theSameElementsAs(Seq(QuantityPrice(5, 5), QuantityPrice(6, 6)))
        result.getPrices("B") should contain.theSameElementsAs(Seq(QuantityPrice(1, 1)))
      }
    }

    it("removedItem") {
      val result = AllPrices()
        .updatedPrice("A", 5, 5)
        .updatedPrice("B", 1, 1)
        .updatedPrice("A", 6, 6)
        .removedItem("A")
      result.containsItem("A") should equal(false)
    }

    describe("removedPrice") {
      it("removes one price") {
        val prices = AllPrices()
          .updatedPrice("A", 5, 5)
          .updatedPrice("A", 6, 6)
          .removedPrice("A", 5)
          .getPrices("A")
        prices should contain.theSameElementsAs(Seq(QuantityPrice(6, 6)))
      }
      it("removes last price of item") {
        val result = AllPrices()
          .updatedPrice("A", 5, 5)
          .removedPrice("A", 5)
        result.getPrices("A").isEmpty should equal(true)
        result.containsItem("A") should equal(false)
      }
    }

    describe("missingPriceForOne") {
      it("with one missing") {
        val result = AllPrices()
          .updatedPrice("A", 5, 5)
          .updatedPrice("B", 1, 1)
        result.missingPriceForOne() should contain.theSameElementsAs(Seq("A"))
      }
      it("with none missing") {
        val result = AllPrices()
          .updatedPrice("A", 1, 5)
          .updatedPrice("B", 1, 1)
        result.missingPriceForOne().isEmpty should equal(true)
      }
    }

  }

}
