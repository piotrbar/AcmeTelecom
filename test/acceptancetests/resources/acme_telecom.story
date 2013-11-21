Story: As a billing controller I want to generate customers' bills and interact with the billing system api.
 
Scenario: A user that did not make any calls is not charged at all
Given users John,Dan with Standard tariff in the database
Given user John did not make any calls
When all the bills are generated
Then the bill for user John contains 0 calls

Scenario: A user receives a bill and is charged for all the calls that he made
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 1 seconds
And user John called user Dan for 2 seconds
When all the bills are generated
Then the bill for user John contains 2 calls

Scenario: The billing system creates a file with bills for all the users
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 1 seconds
And user Dan called user John for 1 seconds
When all the bills are generated
Then the bill for user Dan contains 1 calls
And the bill for user John contains 1 calls