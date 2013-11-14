package com.acmetelecom;

import org.joda.time.DateTime;

class DaytimePeakPeriod {

    // TODO So far we only allow the peak period to start and finish at full
    // hours
    private final int peakStart;
    private final int peakEnd;

    public DaytimePeakPeriod(final int peakStart, final int peakEnd) {
	super();
	this.peakStart = peakStart;
	this.peakEnd = peakEnd;
    }

    @Deprecated
    public DaytimePeakPeriod() {
	super();
	peakStart = 7;
	peakEnd = 19;
    }

    public boolean offPeak(final DateTime time) {
	// final Calendar calendar = Calendar.getInstance();
	// calendar.setTime(time);
	// final int hour = calendar.get(Calendar.HOUR_OF_DAY);

	final int hour = time.getHourOfDay();

	return hour < peakStart || hour >= peakEnd;
    }
}
