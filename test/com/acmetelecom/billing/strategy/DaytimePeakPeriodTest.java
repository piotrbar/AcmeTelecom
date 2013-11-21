package com.acmetelecom.billing.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.billing.strategy.DaytimePeakPeriod;

public class DaytimePeakPeriodTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOffPeak() {

	final DaytimePeakPeriod period = new DaytimePeakPeriod(7, 19);

	final String[] zones = { "UTC", "WET", "PST", "GMT", "EST", "ECT" };
	// Change the default timezone and test the dates
	for (final String zone : zones) {
	    TimeZone.setDefault(TimeZone.getTimeZone(zone));

	    final DateTime timePeak = new DateTime(2013, 11, 12, 8, 10, 10); // Inside
									     // the
									     // peak
	    final DateTime timeOffPeakEarly = new DateTime(2013, 11, 12, 6, 10, 10); // Before
										     // peak
	    final DateTime timeOffPeakLate = new DateTime(2013, 11, 12, 20, 10, 10); // After
										     // peak

	    assertFalse(period.offPeak(timePeak));
	    assertTrue(period.offPeak(timeOffPeakEarly));
	    assertTrue(period.offPeak(timeOffPeakLate));
	}
    }

    @Test
    public void testGetOffPeakSecondsInADay() {
	DaytimePeakPeriod peakPeriod = new DaytimePeakPeriod(7, 19);
	assertEquals(peakPeriod.getOffPeakSecondsInADay(), 12 * 3600);
	peakPeriod = new DaytimePeakPeriod(22, 5);
	assertEquals(peakPeriod.getOffPeakSecondsInADay(), 17 * 3600);
    }

    @Test
    public void testGetPeakSecondsInADay() {
	DaytimePeakPeriod peakPeriod = new DaytimePeakPeriod(7, 19);
	assertEquals(peakPeriod.getPeakSecondsInADay(), 12 * 3600);
	peakPeriod = new DaytimePeakPeriod(22, 5);
	assertEquals(peakPeriod.getPeakSecondsInADay(), 7 * 3600);
    }

    @Test
    public void testGetPeakSeconds() {
	DaytimePeakPeriod peakPeriod = new DaytimePeakPeriod(7, 19);

	// Test call which starts and ends during the peak period
	DateTime startTime = new DateTime(2013, 1, 1, 7, 0, 0);
	DateTime endTime = new DateTime(2013, 1, 1, 9, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 7200);

	// Test call which starts and ends during the off peak period
	startTime = new DateTime(2013, 1, 1, 19, 0, 0);
	endTime = new DateTime(2013, 1, 1, 21, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 0);

	// Test call which starts on peak and ends off peak
	startTime = new DateTime(2013, 1, 1, 18, 0, 0);
	endTime = new DateTime(2013, 1, 1, 20, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 3600);

	// Test call which starts off peak and ends on peak
	startTime = new DateTime(2013, 1, 1, 6, 0, 0);
	endTime = new DateTime(2013, 1, 1, 8, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 3600);

	// Test call which starts off peak and ends off peak with a whole
	// peak period in between
	startTime = new DateTime(2013, 1, 1, 6, 0, 0);
	endTime = new DateTime(2013, 1, 1, 20, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 43200);

	// Test call which starts on peak and ends on peak with a whole off
	// peak period in between
	startTime = new DateTime(2013, 1, 1, 18, 0, 0);
	endTime = new DateTime(2013, 1, 2, 8, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 7200);

	// Test call which lasts spans 2 days
	startTime = new DateTime(2013, 1, 1, 8, 0, 0);
	endTime = new DateTime(2013, 1, 2, 8, 0, 0);
	assertEquals(peakPeriod.getPeakSeconds(startTime, endTime), 43200);

	// ar, month, day, hour, minute, second
    }
}
