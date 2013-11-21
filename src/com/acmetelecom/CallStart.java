package com.acmetelecom;

@Deprecated
public class CallStart extends CallEvent {
    public CallStart(final String caller, final String callee) {
	super(caller, callee, System.currentTimeMillis());
    }
}
