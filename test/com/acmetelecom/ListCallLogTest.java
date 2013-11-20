package com.acmetelecom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListCallLogTest {

    private static final String caller1 = "447722113434";
    private static final String caller2 = "447766511332";
    private static final int caller1Calls = 3;
    private static final int caller2Calls = 1;
    private CallLog test;

    @Before
    public void setUp() throws Exception {
	// Create a test call log
	test = new ListCallLog();

	// Create a bunch of dummy calls and populate the call log
	for (int i = 0; i < caller1Calls; i++) {
	    Call c1 = new Call(caller1, caller2, System.currentTimeMillis());
	    c1.completed(System.currentTimeMillis());
	    test.addCall(c1);
	}

	for (int i = 0; i < caller2Calls; i++) {
	    Call c2 = new Call(caller2, caller1, System.currentTimeMillis());
	    c2.completed(System.currentTimeMillis());
	    test.addCall(c2);
	}
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClearCompletedCalls() {
	assertTrue(test.getCallsForCustomer(caller1).size() > 0);
	assertTrue(test.getCallsForCustomer(caller2).size() > 0);

	// Clear the call log
	test.clearCompletedCalls();

	assertEquals(0, test.getCallsForCustomer(caller1).size());
	assertEquals(0, test.getCallsForCustomer(caller2).size());
    }

    @Test
    public void testAddCall() {
	// Create one extra dummy call for each caller
	Call c1 = new Call(caller1, caller2, System.currentTimeMillis());
	c1.completed(System.currentTimeMillis());
	Call c2 = new Call(caller2, caller1, System.currentTimeMillis());
	c2.completed(System.currentTimeMillis());

	// Add the new calls to the call log
	test.addCall(c1);
	test.addCall(c2);

	assertEquals(caller1Calls + 1, test.getCallsForCustomer(caller1).size());
	assertEquals(caller2Calls + 1, test.getCallsForCustomer(caller2).size());
    }

    @Test
    public void testGetCalls() {
	assertEquals(caller1Calls, test.getCallsForCustomer(caller1).size());
	assertEquals(caller2Calls, test.getCallsForCustomer(caller2).size());
    }
}
