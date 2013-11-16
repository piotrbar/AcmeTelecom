package com.acmetelecom;

import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BillingSystemTest {
    Mockery context = new Mockery();
    final String caller = "1234";
    final String callee = "5678";
    final CallLog callLog = context.mock(CallLog.class);
    final BillingSystem billingSystem = new BillingSystem(callLog);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCallInitiated() throws InvalidCallException {
	context.checking(new Expectations() {
	    {
		oneOf(callLog).callInitiated(caller, callee);
	    }
	});
	billingSystem.callInitiated(caller, callee);
    }

    @Test(expected = InvalidCallException.class)
    public void testOnlyOneActiveCallPerCaller() throws InvalidCallException {
	testCallInitiated();
	billingSystem.callInitiated(caller, callee);
    }

    @Test
    public void testCallCompleted() throws InvalidCallException {
	testCallInitiated();
	context.checking(new Expectations() {
	    {
		oneOf(callLog).callCompleted(caller, callee);
	    }
	});
	billingSystem.callCompleted(caller, callee);
    }

    @Test(expected = InvalidCallException.class)
    public void testOnlyActiveCallCanBeCompleted() throws InvalidCallException {
	billingSystem.callCompleted(caller, callee);
    }

    @Test(expected = InvalidCallException.class)
    public void testActiveCallCanOnlyBeCompletedOnce() throws InvalidCallException {
	testCallCompleted();
	billingSystem.callCompleted(caller, callee);
    }

    @Test
    public void testCreateCustomerBills() {
	fail("Not yet implemented");
    }

}
