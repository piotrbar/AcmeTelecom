Story: As a billing controller I want to generate customers' bills and interact with the billing system api.

Scenario: A user receives a bill with all his calls included
Given user 447722113434 called user 447766511332 for 1 seconds
And user 447722113434 called user 447766511332 for 2 seconds
When all the bills are generated
Then the bill for user 447722113434 contains 2 calls
