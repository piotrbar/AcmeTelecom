package com.acmetelecom;

import java.util.Calendar;
import java.util.Date;

class DaytimePeakPeriod {

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

    public boolean offPeak(final Date time) {
	final Calendar calendar = Calendar.getInstance();
	calendar.setTime(time);
	final int hour = calendar.get(Calendar.HOUR_OF_DAY);
	return hour < peakStart || hour >= peakEnd;
    }
}
