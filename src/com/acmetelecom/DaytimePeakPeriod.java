package com.acmetelecom;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

public class DaytimePeakPeriod {

    private static final int secondsInADay = 24 * 60 * 60;

    // TODO So far we only allow the peak period to start and finish at full
    // hours
    private final int peakStart;
    private final int peakEnd;
    private final int offPeakSeconds;

    public DaytimePeakPeriod(final int peakStart, final int peakEnd) {
	this.peakStart = peakStart;
	this.peakEnd = peakEnd;

	if (peakEnd - peakStart > 0) {
	    offPeakSeconds = (peakEnd - peakStart) * 60 * 60;
	} else {
	    offPeakSeconds = ((24 - peakStart) + peakEnd) * 60 * 60;
	}
    }

    @Deprecated
    public DaytimePeakPeriod() {
	this(7, 19);
    }

    public boolean offPeak(final DateTime time) {
	final int hour = time.getHourOfDay();
	return hour < peakStart || hour >= peakEnd;
    }

    public int getOffPeakSecondsInADay() {
	return offPeakSeconds;
    }

    public int getPeakSecondsInADay() {
	return (24 * 60 * 60) - offPeakSeconds;
    }

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
     * Current implementation assumes calls are no longer than 24hrs.
     * 
     * @param startTime
     * @param endTime
     * @return
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
		return duration - offPeakSeconds;
	    }
	}

    }

    private DateTime getStartOfPeak(DateTime time) {
	if (time.getHourOfDay() >= peakStart) {
	    time = time.plus(Days.days(1));
	}
	return new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), peakStart, 0, 0);
    }

    private DateTime getEndOfPeak(DateTime time) {
	if (time.getHourOfDay() >= peakEnd) {
	    time = time.plus(Days.days(1));
	}
	return new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), peakEnd, 0, 0);
    }

}
