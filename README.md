Problem statement
=================

Consider a store where items have prices per unit but also volume prices. For example, apples may be $1.00 each or 4 for $3.00. Implement a point-of-sale scanning API that accepts an arbitrary ordering of products (similar to what would happen at a checkout line) and then returns the correct total price for an entire shopping cart based on the per unit prices or the volume prices as applicable.

Here are the products listed by code and the prices to use (there is no sales tax):

Ensure that the assignment is submitted with working test cases
Product Code 	Price
A 	$2 each or 4 for $7
B 	$12
C 	$1.25 each or $6 for a six-pack
D 	$.15

There should be a top level point of sale terminal service object that looks something like the pseudo-code below. You are free to design and implement the rest of the code however you wish, including how you specify the prices in the system:

terminal.setPricing(...)
terminal.scan("A")
terminal.scan("C")
... etc.
result = terminal.total

    Here are the minimal inputs you should use for your test cases. These test cases must be shown to work in your program:
    Scan these items in this order: ABCDABAA; Verify the total price is $32.40.
    Scan these items in this order: CCCCCCC; Verify the total price is $7.25.
    Scan these items in this order: ABCD; Verify the total price is $15.40.


About the project
==========

This is a pretty staightforward scala application. It requires sbt.
The tests can be run with
`sbt clean test`

The working API of the program is demonstrated by the tests in integration.IntegrationTest.
The requested tests are in that file.

Since all the code is immutable, when a price or order is updated, it returns a 
new price or order object which reflects the update.

To start using this API, you call scanners.PriceScanner() to get an instance of 
PriceScanner. You can then add prices. Then you can call priceScanner.newOrder() to
create an order. The you can scan items to add to the order. You can call order.total()
at any time.

Any item which has prices added to it, must have a price for an individual item. If you
create an order without meeting that requirement, then priceScanner.newOrder() returns
an error.

For example:
    
    PriceScanner()
        .scan(itemCode = "A", quantity = 6, price = 1)
        .newOrder()

The result is: Left(MissingPricesForOne(Set(A)))

There is also an error returned if an item is scanned which does not exist in the 
PriceScanner.

For example:

    PriceScanner()
      .scan(itemCode = "A", quantity = 1, price = 1)
      .newOrder()
      .flatMap(_.scan("B"))
      
The result is: Left(InvalidCode(B))