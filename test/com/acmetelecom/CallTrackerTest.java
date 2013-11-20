package com.acmetelecom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.exceptions.IllegalCallException;

public class CallTrackerTest {
    private final Mockery context = new Mockery();
    private final CallLog callLog = context.mock(CallLog.class);
    private CallTracker callTracker = null;

    private final String caller = "1234";
    private final String caller2 = "1235";
    private final String callee = "5678";

    @Before
    public void setUp() throws Exception {
	callTracker = new CallTracker(callLog);
    }

    @Test
    public void testInitiatingCall() throws IllegalCallException {
	final Call initiatedCall = callTracker.callInitiated(caller, callee);

	assertEquals(initiatedCall.caller(), caller);
	assertEquals(initiatedCall.callee(), callee);
	final boolean callStartedBeforeNow = initiatedCall.startTime().getTime() <= System.currentTimeMillis();
	assertTrue(callStartedBeforeNow);
    }

    @Test
    public void testCompletingCall() throws IllegalCallException {
	final Call initiatedCall = callTracker.callInitiated(caller, callee);

	context.checking(new Expectations() {
	    {
		oneOf(callLog).addCall(initiatedCall);
	    }
	});

	final Call completedCall = callTracker.callCompleted(caller, callee);

	assertEquals(completedCall.callee(), completedCall.callee());
	assertEquals(completedCall.caller(), completedCall.caller());
	final boolean callCompletedBeforeNow = completedCall.endTime().getTime() <= System.currentTimeMillis();
	assertTrue(callCompletedBeforeNow);
    }

    @Test(expected = IllegalCallException.class)
    public void testCantRecompleteCall() throws IllegalCallException {
	final Call call = callTracker.callInitiated(caller, callee);
	context.checking(new Expectations() {
	    {
		allowing(callLog).addCall(call);
	    }
	});
	callTracker.callCompleted(caller, callee);
	context.checking(new Expectations() {
	    {
		never(callLog).addCall(call);
	    }
	});
	callTracker.callCompleted(caller, callee);
    }

    @Test(expected = IllegalCallException.class)
    public void testCantCompleteNeverInitiatedCall() throws IllegalCallException {
	callTracker.callCompleted(caller, callee);
    }

    @Test(expected = IllegalCallException.class)
    public void testOnlyOneActiveCallPerUser() throws IllegalCallException {
	callTracker.callInitiated(caller, callee);
	callTracker.callInitiated(caller, callee);
    }

    @Test(expected = IllegalCallException.class)
    public void testOnlyOneActiveCallPerCallee() throws IllegalCallException {
	callTracker.callInitiated(caller, callee);
	callTracker.callInitiated(caller2, callee);
    }

    @Test(expected = IllegalCallException.class)
    public void testCannotCallYourself() throws IllegalCallException {
	callTracker.callInitiated(caller, caller);
    }

    @Test
    public void testCallerInProgress() throws IllegalCallException {
	callTracker.callInitiated(caller, callee);
	assertTrue(callTracker.callInProgress(caller));
    }

    @Test
    public void testCallerNotInProgress() {
	assertFalse(callTracker.callInProgress(caller));
    }

    @Test
    public void testCompletedCallerNotInProgress() throws IllegalCallException {
	callTracker.callInitiated(caller, callee);
	context.checking(new Expectations() {
	    {
		ignoring(callLog);
	    }
	});
	callTracker.callCompleted(caller, callee);
	assertFalse(callTracker.callInProgress(caller));
    }
}