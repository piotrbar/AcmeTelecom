package com.acmetelecom;

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO There should be a factory or builder to create those calls with a meaningful fluid interface call. 
// The constructor parameters are super difficult to understand
public class Call {

    private final String caller;
    private final String callee;
    private final Long timeStart;
    private Long timeEnd;

    /**
     * 
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
	return SimpleDateFormat.getInstance().format(new Date(timeStart));
    }

    public Date startTime() {
	return new Date(timeStart);
    }

    public Date endTime() {
	return new Date(timeEnd);
    }

    public void completed(final long timeEnd) {
	this.timeEnd = timeEnd;
    }

}