package com.acmetelecom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.TimeZone;

import org.joda.time.DateTime;
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

	// Changing default TimeZone does not make any difference here so it is
	// pointless. new Date() is timezone agnostic,
	// it only makes a difference when you things like .toString() or
	// Calender methods
	for (final String zone : zones) {
	    TimeZone.setDefault(TimeZone.getTimeZone(zone));

	    // final Date timePeak = new Date(2013, 11, 12, 8, 10, 10); //
	    // Inside the peak
	    // final Date timeOffPeakEarly = new Date(2013, 11, 12, 6, 10, 10);
	    // // Before peak
	    // final Date timeOffPeakLate = new Date(2013, 11, 12, 20, 10, 10);
	    // // After peak

	    final DateTime timePeak = new DateTime(2013, 11, 12, 8, 10, 10);
	    final DateTime timeOffPeakEarly = new DateTime(2013, 11, 12, 6, 10, 10);
	    final DateTime timeOffPeakLate = new DateTime(2013, 11, 12, 20, 10, 10);

	    assertFalse(period.offPeak(timePeak));
	    assertTrue(period.offPeak(timeOffPeakEarly));
	    assertTrue(period.offPeak(timeOffPeakLate));
	}
    }

}
