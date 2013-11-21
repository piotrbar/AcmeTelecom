package com.acmetelecom;

public abstract class Call {

    private final String caller;
    private final String callee;

    /**
     * A new constructor for the call class. A call is incomplete until the
     * timeEnd is set.
     */
    public Call(final String caller, final String callee) {
	this.caller = caller;
	this.callee = callee;
    }

    public String caller() {
	return caller;
    }

    public String callee() {
	return callee;
    }

    public abstract String date();

    public abstract Long startTime();

}