package com.acmetelecom;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.exceptions.IllegalCallException;

public class BillingSystemTest {

    private final Mockery context = new JUnit4Mockery() {
	{
	    setImposteriser(ClassImposteriser.INSTANCE);
	}
    };

    private final String caller = "1234";
    private final String callee = "5678";
    private BillingSystem billingSystem;
    private Biller biller;
    private CallTracker tracker;

    @Before
    public void setUp() {
	biller = context.mock(Biller.class);
	tracker = context.mock(CallTracker.class);
	billingSystem = new BillingSystem(biller, tracker);
    }

    @Test
    public void testCallInitiated() throws IllegalCallException {
	context.checking(new Expectations() {
	    {
		oneOf(tracker).callInitiated(caller, callee);
	    }
	});
	billingSystem.callInitiated(caller, callee);
	context.assertIsSatisfied();
    }

    @Test
    public void testCallCompleted() throws IllegalCallException {
	testCallInitiated();
	context.checking(new Expectations() {
	    {
		oneOf(tracker).callCompleted(caller, callee);
	    }
	});
	billingSystem.callCompleted(caller, callee);
	context.assertIsSatisfied();
    }

    @Test
    public void testCreateCustomerBills() {
	context.checking(new Expectations() {
	    {
		oneOf(biller).createCustomerBills();
	    }
	});
	billingSystem.createCustomerBills();
	context.assertIsSatisfied();
    }

}
