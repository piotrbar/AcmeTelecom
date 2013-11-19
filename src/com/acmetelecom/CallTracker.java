package com.acmetelecom;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

public class CallTracker {

    private final Map<String, Call> activeCalls;
    private final CallLog callLog;

    @Autowired
    public CallTracker(final CallLog callLog) {
	activeCalls = Maps.newHashMap();
	this.callLog = callLog;
    }

    public Call callInitiated(final String caller, final String callee) {
	Call call = null;
	if (!callInProgress(caller)) {
	    call = new Call(caller, callee, System.currentTimeMillis());
	    activeCalls.put(caller, call);
	} else {
	    // TODO
	}

	return call;
    }

    public boolean callInProgress(final String caller) {
	return activeCalls.containsKey(caller);
    }

    public Call callCompleted(final String caller, final String callee) {
	final Call call = activeCalls.get(caller);
	if (call != null && call.callee().equals(callee)) {
	    call.completed(System.currentTimeMillis());
	    callLog.addCall(call);
	    activeCalls.remove(caller);
	    return call;
	} else {
	    // TODO
	    return null;
	}

    }
}
