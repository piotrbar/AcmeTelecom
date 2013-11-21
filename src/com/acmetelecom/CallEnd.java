package com.acmetelecom;

@Deprecated
public class CallEnd extends CallEvent {
    public CallEnd(final String caller, final String callee) {
	super(caller, callee, System.currentTimeMillis());
    }
}
