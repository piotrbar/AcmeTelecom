package com.acmetelecom;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// TODO There should be a factory or builder to create those calls with a meaningful fluid interface call. 
// The constructor parameters are super difficult to understand
public class Call {

    private final String caller;
    private final String callee;
    private final Long timeStart;
    private Long timeEnd;

    /**
     * A new constructor for the call class. A call is incomplete until the
     * timeEnd is set.
     */
    public Call(final String caller, final String callee, final long timeStart) {
	this.caller = caller;
	this.callee = callee;
	this.timeStart = timeStart;
	timeEnd = null;
    }

    /**
     * @deprecated: use {@link #Call(String, String, long, long)} instead.
     */
    @Deprecated
    public Call(final CallEvent start, final CallEvent end) {
	assert (start.getCaller().equals(end.getCaller()));
	assert (start.getCallee().equals(end.getCallee()));

	caller = start.getCaller();
	callee = start.getCallee();
	timeStart = start.time();
	timeEnd = end.time();
    }

    public Call(final String caller, final String callee, final long timeStart, final long timeEnd) {
	this.caller = caller;
	this.callee = callee;
	this.timeStart = timeStart;
	this.timeEnd = timeEnd;
    }

    public String caller() {
	return caller;
    }

    public String callee() {
	return callee;
    }

    public int durationSeconds() {
	return (int) (((timeEnd - timeStart) / 1000));
    }

    public String date() {
	final DateTimeFormatter dtf = DateTimeFormat.shortDateTime();
	return new DateTime(timeStart).toString(dtf);
    }

    public long startTime() {
	return timeStart;
    }

    public long endTime() {
	return timeEnd;
    }

    public void completed(final long timeEnd) {
	this.timeEnd = timeEnd;
    }

}