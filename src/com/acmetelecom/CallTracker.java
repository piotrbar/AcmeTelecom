package com.acmetelecom;

import java.util.HashMap;
import java.util.Map;

public class CallTracker {

    private final Map<String, Call> activeCalls;
    private final CallLog callLog;

    public CallTracker(final CallLog callLog) {
	this.activeCalls = new HashMap<String, Call>();
	this.callLog = callLog;
    }

    public void callInitiated(final String caller, final String callee) {
	if (!this.callInProgress(caller)) {
	    this.activeCalls.put(caller, new Call(caller, callee, System.currentTimeMillis()));
	} else {
	    // TODO
	}
    }

    public boolean callInProgress(final String caller) {
	return this.activeCalls.containsKey(caller);
    }

    public void callCompleted(final String caller, final String callee) {
	final Call call = this.activeCalls.get(caller);
	if (call != null && call.callee().equals(callee)) {
	    call.completed(System.currentTimeMillis());
	    this.callLog.addCall(call);
	    this.activeCalls.remove(caller);
	} else {
	    // TODO
	}
    }
}
