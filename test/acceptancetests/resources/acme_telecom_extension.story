Story: As a billing controller I want to generate customers' bills and charge them according to the correct tariff.
The standard tariff fees are used for these scenarios. 0.2p/s for offpeak and 0.5p/s for peak. Peak starts at 11am and finishes at 7pm

Scenario: A user starts and finishes a call in the offpeak period
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 600 seconds at 7pm
When all the bills are generated
Then the total for user John is 1.20

Scenario: A user starts and finishes a call in the peak period
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 600 seconds at 11am
When all the bills are generated
Then the total for user John is 3.00

Scenario: A user starts a call in the off-peak period and finishes in the peak period
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 7200 seconds at 10am
When all the bills are generated
Then the total for user John is 25.20

Scenario: A user starts a call in the peak period and finishes in the off-peak period
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 7200 seconds at 6pm
When all the bills are generated
Then the total for user John is 25.20

Scenario: A user starts a call in the peak period and finishes in the off-peak period
Given users John,Dan with Standard tariff in the database
And user John called user Dan for 180000 seconds at 6pm
When all the bills are generated
Then the total for user John is 543.60

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