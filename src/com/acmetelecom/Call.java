package com.acmetelecom;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	this.timeEnd = null;
    }

    /**
     * @deprecated: use {@link #Call(String, String, long, long)} instead.
     */
    @Deprecated
    public Call(final CallEvent start, final CallEvent end) {
	assert (start.getCaller().equals(end.getCaller()));
	assert (start.getCallee().equals(end.getCallee()));

	this.caller = start.getCaller();
	this.callee = start.getCallee();
	this.timeStart = start.time();
	this.timeEnd = end.time();
    }

    public String caller() {
	return this.caller;
    }

    public String callee() {
	return this.callee;
    }

    public int durationSeconds() {
	return (int) (((this.timeEnd - this.timeStart) / 1000));
    }

    public String date() {
	return SimpleDateFormat.getInstance().format(new Date(this.timeStart));
    }

    public Date startTime() {
	return new Date(this.timeStart);
    }

    public Date endTime() {
	return new Date(this.timeEnd);
    }

    public void completed(final long timeEnd) {
	this.timeEnd = timeEnd;
    }

}