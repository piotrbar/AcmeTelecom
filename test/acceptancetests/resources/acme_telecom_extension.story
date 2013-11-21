Story: As a billing controller I want to generate customers' bills and charge them according to the correct tariff.
The standard tariff fees are used for these scenarios. 0.2p/s for offpeak and 0.5p/s for peak. Peak starts at 7am and finishes at 7pm

Scenario: A user starts and finishes a call in the offpeak period
Given users John,Dan with Standard tariff exist in the database
And user John called user Dan for 600 seconds at 7pm
When all the bills are generated
Then the total for user John is 1.20

Scenario: A user starts and finishes a call in the peak period
Given users John,Dan with Standard tariff exist in the database
And user John called user Dan for 600 seconds at 11am
When all the bills are generated
Then the total for user John is 3.00

Scenario: A user starts a call in the peak period and finishes in the offpeak period
Given users John,Dan with Standard tariff exist in the database
And user John called user Dan for 7200 seconds at 10am
When all the bills are generated
Then the total for user John is 25.20

Scenario: A user starts a call in the offpeak period and finishes in the peak period
Given users John,Dan with Standard tariff exist in the database
And user John called user Dan for 7200 seconds at 6pm
When all the bills are generated
Then the total for user John is 25.20