package com.acmetelecom;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class CallTest {

    Mockery context = new JUnit4Mockery() {
	{
	    setImposteriser(ClassImposteriser.INSTANCE); // Allow to mock
							 // concrete classes
	}
    };

    // Mock meta-data
    final String caller = "447722113434";
    final String callee = "447766511332";

    final long time = System.currentTimeMillis();
    final long timeStart = time + 5000;
    final long timeEnd = time - 5000;

    // Mock the CallStart and CallEnd events
    final CallStart start = context.mock(CallStart.class);
    final CallEnd end = context.mock(CallEnd.class);

    @Before
    public void setUp() {
	// Set up the call start event
	context.checking(new Expectations() {
	    {
		allowing(start).getCaller();
		will(returnValue(caller));
		allowing(start).getCallee();
		will(returnValue(callee));
		allowing(start).time();
		will(returnValue(timeStart));
	    }
	});

	// Set up the call end event
	context.checking(new Expectations() {
	    {
		allowing(start).getCaller();
		will(returnValue(caller));
		allowing(start).getCallee();
		will(returnValue(callee));
		allowing(start).time();
		will(returnValue(timeEnd));
	    }
	});
    }

    @Test
    public void testCallee() {
	// Creating a Call test object from start to end
	final Call call = new Call(start, end);

    }

    @Test
    public void testDurationSeconds() {
	final MathOp a = new MathOp();
	assertEquals(-4, a.sub(-3, 1));
    }
}