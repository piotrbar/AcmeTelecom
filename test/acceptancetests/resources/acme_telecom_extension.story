Story: As a billing controller I want to generate customers' bills and charge them according to the correct tariff.

Lifecycle: 
Before:
Given peak starts at 11am and finishes at 7pm
Given peak charge is 30 p/min and offpeak charge is 15 p/min

Scenario: A user starts and finishes a call in the offpeak period
Given user 447722113434 called user 447766511332 for 600 seconds at 7pm
When all the bills are generated
Then the total for user 447722113434 is 1.50

Scenario: A user starts and finishes a call in the peak period
Given user 447722113434 called user 447766511332 for 600 seconds at 11am
When all the bills are generated
Then the total for user 447722113434 is 3.00

Scenario: A user starts a call in the peak period and finishes in the offpeak period
Given user 447722113434 called user 447766511332 for 7200 seconds at 10am
When all the bills are generated
Then the total for user 447722113434 is 27.00

Scenario: A user starts a call in the offpeak period and finishes in the peak period
Given user 447722113434 called user 447766511332 for 7200 seconds at 6pm
When all the bills are generated
Then the total for user 447722113434 is 27.00