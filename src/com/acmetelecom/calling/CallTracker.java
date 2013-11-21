package com.acmetelecom.calling;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.exceptions.IllegalCallException;
import com.google.common.collect.Maps;

/**
 * Keeps track of active and completed calls.
 */
public class CallTracker {

    // Maps a customer's phone number to a active call
    private final Map<String, ActiveCall> activeCalls;
    // Completed calls
    private final CallLog callLog;

    private static final Logger LOG = LogManager.getLogger(CallTracker.class);

    @Autowired
    public CallTracker(final CallLog callLog) {
	activeCalls = Maps.newHashMap();
	this.callLog = callLog;
    }

    /**
     * Initiates the call between caller and callee and marks it as active.
     */
    public ActiveCall callInitiated(final String caller, final String callee) throws IllegalCallException {
	// Calls to ourselves are not allowed
	if (caller.equals(callee)) {
	    final String errorMessage = String.format("User %s cannot initiate a call to him/herself.", caller);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	// Customers that are currently in a call can't take another one
	if (callInProgress(caller) || callInProgress(callee)) {
	    final String errorMessage = String.format("One of the users: %s, %s is in the busy state.", caller, callee);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	// Create an active call starting at this very moment
	final ActiveCall activeCall = new ActiveCall(caller, callee, System.currentTimeMillis());
	activeCalls.put(caller, activeCall);
	activeCalls.put(callee, activeCall);

	return activeCall;
    }

    /**
     * Checks whether a user is currently in a call.
     */
    public boolean callInProgress(final String user) {
	return activeCalls.containsKey(user);
    }

    /**
     * Marks a call between caller and callee as completed.
     */
    public FinishedCall callCompleted(final String caller, final String callee) throws IllegalCallException {
	// Verify that there is a currently active call between caller and
	// callee
	final ActiveCall activeCall = activeCalls.get(caller);
	if (activeCall == null || !activeCall.callee().equals(callee) || !activeCall.caller().equals(caller)) {
	    final String errorMessage = String.format("User %s has not initiated a call with %s.", caller, callee);
	    LOG.error(errorMessage);
	    throw new IllegalCallException(errorMessage);
	}

	// Mark the active call as completed and add it to the call log
	FinishedCall finishedCall = new FinishedCall(activeCall, System.currentTimeMillis());
	callLog.addCall(finishedCall);
	activeCalls.remove(caller);
	activeCalls.remove(callee);

	return finishedCall;
    }
}
