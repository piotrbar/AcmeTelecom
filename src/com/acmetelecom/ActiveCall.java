package com.acmetelecom;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ActiveCall extends Call {

    private final Long timeStart;

    public ActiveCall(final String caller, final String callee, final Long timeStart) {
	super(caller, callee);
	this.timeStart = timeStart;
    }

    @Override
    public String date() {
	final DateTimeFormatter dtf = DateTimeFormat.shortDateTime();
	return new DateTime(timeStart).toString(dtf);
    }

    @Override
    public Long startTime() {
	return timeStart;
    }

}
