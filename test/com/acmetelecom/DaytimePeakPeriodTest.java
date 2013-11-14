package com.acmetelecom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

	    final Date timePeak = new Date(2013, 11, 12, 8, 10, 10); // Inside the peak
	    final Date timeOffPeakEarly = new Date(2013, 11, 12, 6, 10, 10); // Before peak
	    final Date timeOffPeakLate = new Date(2013, 11, 12, 20, 10, 10); // After peak

	    assertFalse(period.offPeak(timePeak));
	    assertTrue(period.offPeak(timeOffPeakEarly));
	    assertTrue(period.offPeak(timeOffPeakLate));
	}
    }

}
