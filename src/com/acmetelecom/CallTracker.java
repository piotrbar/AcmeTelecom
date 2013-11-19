package com.acmetelecom;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.exceptions.IllegalCallException;
import com.google.common.collect.Maps;

public class CallTracker {

    private final Map<String, Call> activeCalls;
    private final CallLog callLog;

    private static final Logger LOG = LogManager.getLogger(CallTracker.class);

    @Autowired
    public CallTracker(final CallLog callLog) {
	activeCalls = Maps.newHashMap();
	this.callLog = callLog;
    }

    public Call callInitiated(final String caller, final String callee) throws IllegalCallException {
	if (caller.equals(callee)) {
	    final String errorMessage = String.format("User %s cannot initiate a call to him/herself.", caller);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	if (callInProgress(caller) || callInProgress(callee)) {
	    final String errorMessage = String.format("One of the users: %s, %s is in the busy state.", caller, callee);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	final Call call = new Call(caller, callee, System.currentTimeMillis());
	activeCalls.put(caller, call);
	return call;
    }

    public boolean callInProgress(final String caller) {
	return activeCalls.containsKey(caller);
    }

    public Call callCompleted(final String caller, final String callee) throws IllegalCallException {
	final Call call = activeCalls.get(caller);
	if (call == null || !call.callee().equals(callee)) {
	    final String errorMessage = String.format("User %s has not initiated a call with %s.", caller, callee);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	call.completed(System.currentTimeMillis());
	callLog.addCall(call);
	activeCalls.remove(caller);
	return call;
    }
}
