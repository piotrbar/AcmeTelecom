package com.acmetelecom.calling;

public abstract class Call {

    private final String caller;
    private final String callee;

    Call(final String caller, final String callee) {
	this.caller = caller;
	this.callee = callee;
    }

    public String caller() {
	return caller;
    }

    public String callee() {
	return callee;
    }

    /**
     * Returns the human readable call start time
     */
    public abstract String date();

    /**
     * Returns the call start time
     */
    public abstract Long startTime();

}