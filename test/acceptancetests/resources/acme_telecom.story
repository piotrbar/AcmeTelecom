Story: As a billing controller I want to generate customers' bills and interact with the billing system api.

Scenario: A user that did not made any calls is not charged at all
When all the bills are generated
Then the bill for user 447722113434 contains 0 calls

Scenario: A user receives a bill and is charged for all the calls that he made
Given user 447722113434 called user 447766511332 for 1 seconds
And user 447722113434 called user 447766511332 for 2 seconds
When all the bills are generated
Then the bill for user 447722113434 contains 2 calls

Scenario: The billing system creates a file with bills for all the users
Given user 447722113434 called user 447766511332 for 1 seconds
And user 447766511332 called user 447722113434 for 1 seconds
When all the bills are generated
Then the bill for user 447722113434 contains 1 calls
And the bill for user 447722113434 contains 1 calls

Scenario: A user cannot call himself
When user 447722113434 calls user 447722113434
Then the last call is rejected

Scenario: Two users cannot call the same person at the same time
When user 447722113434 calls user 447722113434
And user 2 calls user 447722113434
Then the last call is rejected

Scenario: One person cannot call two people at the same time
When user 447722113434 calls user 447722113434
And user 447722113434 calls user 2
Then the last call is rejected
