package com.acmetelecom.calling;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class FinishedCall extends Call {

    private final Long timeStart;
    private final Long timeEnd;

    /**
     * A finished call is just an active call that has ended
     */
    public FinishedCall(final ActiveCall call, final Long timeEnd) {
	super(call.caller(), call.callee());
	this.timeStart = call.startTime();
	this.timeEnd = timeEnd;
    }

    /**
     * This constructor is used for testing purposes
     */
    public FinishedCall(final String caller, final String callee, final Long timeStart, final Long timeEnd) {
	super(caller, callee);
	this.timeStart = timeStart;
	this.timeEnd = timeEnd;
    }

    /**
     * Gives the duration of the call in seconds.
     */
    public int durationSeconds() {
	return (int) (((timeEnd - timeStart) / 1000));
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

    public Long endTime() {
	return timeEnd;
    }

}
