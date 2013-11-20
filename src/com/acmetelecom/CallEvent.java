package com.acmetelecom;

public abstract class CallEvent {
    private final String caller;
    private final String callee;
    private final long time;

    public CallEvent(final String caller, final String callee, final long timeStamp) {
	this.caller = caller;
	this.callee = callee;
	time = timeStamp;
    }

    public String getCaller() {
	return caller;
    }

    public String getCallee() {
	return callee;
    }

    public long time() {
	return time;
    }
}
