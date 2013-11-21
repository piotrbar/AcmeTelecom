package com.acmetelecom;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

public class DaytimePeakPeriod {

    private static final int secondsInADay = 24 * 60 * 60;

    private final int peakStart;
    private final int peakEnd;
    private final int peakSeconds;

    public DaytimePeakPeriod(final int peakStart, final int peakEnd) {
	this.peakStart = peakStart;
	this.peakEnd = peakEnd;

	if (peakEnd - peakStart > 0) {
	    peakSeconds = (peakEnd - peakStart) * 60 * 60;
	} else {
	    peakSeconds = ((24 - peakStart) + peakEnd) * 60 * 60;
	}

    }

    @Deprecated
    public DaytimePeakPeriod() {
	this(7, 19);
    }

    /**
     * @param time
     * @return True if the time is within off peak hours. False if the time is
     *         within peak hours.
     */
    public boolean offPeak(final DateTime time) {
	final int hour = time.getHourOfDay();
	return hour < peakStart || hour >= peakEnd;
    }

    /**
     * @return The total number of seconds which which fall within the off peak
     *         time period during a day
     */
    public int getOffPeakSecondsInADay() {
	return (24 * 60 * 60) - peakSeconds;
    }

    /**
     * @return The total number of seconds which fall within the peak time
     *         period during a day
     */
    public int getPeakSecondsInADay() {
	return peakSeconds;
    }

    /**
     * Calculates the total number of seconds which fall within the peak time
     * period given a startTime and endTime
     * 
     * @param startTime
     *            the start DateTime
     * @param endTime
     *            the end DateTime
     * @return the total number of seconds that are on peak between the start
     *         and end times
     */
    public int getPeakSeconds(final DateTime startTime, final DateTime endTime) {

	DateTime newEndTime = new DateTime(endTime);
	Interval interval = new Interval(startTime, newEndTime);
	int peakSeconds = 0;

	while (interval.toDuration().getStandardSeconds() > secondsInADay) {
	    peakSeconds += getPeakSecondsInADay();
	    newEndTime = newEndTime.minusDays(1);
	    interval = new Interval(startTime, newEndTime);
	}

	peakSeconds += getPeakSeconds24(startTime, newEndTime);

	return peakSeconds;

    }

    /**
     * Helper function for getPeakSeconds. Takes two DateTimes which are within
     * 24 hours of each other and returns the number of on peak seconds.
     */
    private int getPeakSeconds24(final DateTime startTime, final DateTime endTime) {
	// Case 1: Both times are within off peak periods
	if (offPeak(startTime) && offPeak(endTime)) {
	    if (endTime.isBefore(getStartOfPeak(startTime))) {
		return 0;
	    } else {
		return getPeakSecondsInADay();
	    }
	}
	// Case 2: The first time is off peak and the second is peak
	if (offPeak(startTime) && !offPeak(endTime)) {
	    final DateTime peakStartTime = getStartOfPeak(startTime);
	    final Interval interval = new Interval(peakStartTime, endTime);
	    return (int) interval.toDuration().getStandardSeconds();
	}
	// Case 3: The first time is on peak and the second if off peak
	if (!offPeak(startTime) && offPeak(endTime)) {
	    final DateTime peakEndTime = getEndOfPeak(startTime);
	    final Interval interval = new Interval(startTime, peakEndTime);
	    return (int) interval.toDuration().getStandardSeconds();
	}
	// Case 4: Both times are on peak
	else {
	    final Interval interval = new Interval(startTime, endTime);
	    int duration = (int) interval.toDuration().getStandardSeconds();
	    if (endTime.isBefore(getEndOfPeak(startTime))) {
		return duration;
	    } else {
		return duration - getOffPeakSecondsInADay();
	    }
	}
    }

    /**
     * Given a time, returns the time at which the next transition to the peak
     * period will occur
     */
    private DateTime getStartOfPeak(DateTime time) {
	if (time.getHourOfDay() >= peakStart) {
	    time = time.plus(Days.days(1));
	}
	return new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), peakStart, 0, 0);
    }

    /**
     * Given a time, returns the time at which the next transition to the off
     * peak period will occur
     */
    private DateTime getEndOfPeak(DateTime time) {
	if (time.getHourOfDay() >= peakEnd) {
	    time = time.plus(Days.days(1));
	}
	return new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), peakEnd, 0, 0);
    }

}
