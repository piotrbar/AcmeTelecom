package com.acmetelecom;

import java.util.HashMap;
import java.util.Map;

public class CallTracker {

    private final Map<String, Call> activeCalls;
    private final CallLog callLog;

    public CallTracker(final CallLog callLog) {
	activeCalls = new HashMap<String, Call>();
	this.callLog = callLog;
    }

    public void callInitiated(final String caller, final String callee) {
	if (!callInProgress(caller)) {
	    activeCalls.put(caller, new Call(caller, callee, System.currentTimeMillis()));
	} else {
	    // TODO
	}
    }

    public boolean callInProgress(final String caller) {
	return activeCalls.containsKey(caller);
    }

    public void callCompleted(final String caller, final String callee) {
	final Call call = activeCalls.get(caller);
	if (call != null && call.callee().equals(callee)) {
	    call.complete(System.currentTimeMillis());
	    callLog.addCall(call);
	    activeCalls.remove(caller);
	} else {
	    // TODO
	}
    }
}
