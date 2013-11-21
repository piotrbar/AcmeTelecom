Story: As a billing controller I want to check that the invalid calls are not registered in customer bills.

Scenario: A user cannot call himself
Given users John,Dan with Standard tariff in the database
When user Dan calls user Dan
And all the bills are generated
Then the bill for user John contains 0 calls

Scenario: Two users cannot call the same person at the same time
Given users John,Dan,Robert with Standard tariff in the database
When user Dan calls user Robert
And user John calls user Robert
And all the bills are generated
Then the bill for user Dan contains 0 calls
And the bill for user John contains 0 calls

Scenario: One person cannot call two people at the same time
Given users John,Dan,Robert with Standard tariff in the database
When user Dan calls user John
And user Dan calls user Robert
And user Dan finishes a call with user John
And all the bills are generated
Then the bill for user Dan contains 1 calls