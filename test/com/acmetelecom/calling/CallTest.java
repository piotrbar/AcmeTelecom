package com.acmetelecom.calling;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.calling.FinishedCall;

public class CallTest {

    final String caller = "447722113434";
    final String callee = "447766511332";

    final Long time = System.currentTimeMillis();
    final Long duration = 10000L;
    final Long startTime = time - duration / 2;
    final Long endTime = time + duration / 2;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testDurationSeconds() {
	final FinishedCall call = new FinishedCall(caller, callee, startTime, endTime);
	assertEquals(duration / 1000, call.durationSeconds());
    }

    @Test
    public void testStartTime() {
	final FinishedCall call = new FinishedCall(caller, callee, startTime, endTime);
	assertEquals(startTime, call.startTime());
    }

    @Test
    public void testEndTime() {
	final FinishedCall call = new FinishedCall(caller, callee, startTime, endTime);
	assertEquals(endTime, call.endTime());
    }

}