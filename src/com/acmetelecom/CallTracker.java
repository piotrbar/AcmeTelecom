package com.acmetelecom;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.exceptions.IllegalCallException;
import com.google.common.collect.Maps;

public class CallTracker {

    private final Map<String, ActiveCall> activeCalls;
    private final CallLog callLog;

    private static final Logger LOG = LogManager.getLogger(CallTracker.class);

    @Autowired
    public CallTracker(final CallLog callLog) {
	activeCalls = Maps.newHashMap();
	this.callLog = callLog;
    }

    public ActiveCall callInitiated(final String caller, final String callee) throws IllegalCallException {
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

	final ActiveCall activeCall = new ActiveCall(caller, callee, System.currentTimeMillis());
	activeCalls.put(caller, activeCall);
	activeCalls.put(callee, activeCall);

	return activeCall;
    }

    public boolean callInProgress(final String user) {
	return activeCalls.containsKey(user);
    }

    public FinishedCall callCompleted(final String caller, final String callee) throws IllegalCallException {
	final ActiveCall activeCall = activeCalls.get(caller);
	if (activeCall == null || !activeCall.callee().equals(callee) || !activeCall.caller().equals(caller)) {
	    final String errorMessage = String.format("User %s has not initiated a call with %s.", caller, callee);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	FinishedCall finishedCall = new FinishedCall(activeCall, System.currentTimeMillis());
	callLog.addCall(finishedCall);
	activeCalls.remove(caller);
	activeCalls.remove(callee);

	return finishedCall;
    }
}
