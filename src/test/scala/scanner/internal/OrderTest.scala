package scanner.internal

import org.scalatest.{FunSpec, Matchers}
import scanner.internal.Order.ItemQuantity

class OrderTest extends FunSpec with Matchers {
  describe("Order") {
    describe("incrementItem") {
      it("one element") {
        val order = Order().incrementItem("A")
        (order.fullOrder should contain).theSameElementsInOrderAs(Seq(ItemQuantity("A", 1)))
      }
      it("two elements") {
        val order = Order()
          .incrementItem("A")
          .incrementItem("B")
        (order.fullOrder should contain).theSameElementsInOrderAs(Seq(ItemQuantity("A", 1), ItemQuantity("B", 1)))
        (order.fullOrder should contain).theSameElementsInOrderAs(Seq(ItemQuantity("A", 1), ItemQuantity("B", 1)))
      }
      it("multiple elements") {
        val order = Order()
          .incrementItem("A")
          .incrementItem("B")
          .incrementItem("A")
        order.fullOrder should contain.theSameElementsInOrderAs(Seq(ItemQuantity("A", 2), ItemQuantity("B", 1)))
      }
    }
  }
}
