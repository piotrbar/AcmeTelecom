package com.acmetelecom;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class ListCallLog implements CallLog {
    Map<String, List<Call>> callHistory;

    public ListCallLog() {
	callHistory = Maps.newHashMap();
    }

    @Override
    public void clearCompletedCalls() {
	callHistory.clear();
    }

    @Override
    public void addCall(final Call c) {
	List<Call> callerHistory = callHistory.get(c.caller());
	if (callerHistory == null) {
	    callerHistory = new LinkedList<Call>();
	}
	callerHistory.add(c);
	callHistory.put(c.caller(), callerHistory);
    }

    @Override
    public Iterable<Call> getCallsForCustomer(final String caller) {
	if (callHistory.containsKey(caller)) {
	    return callHistory.get(caller);
	} else {
	    return new LinkedList<Call>();
	}
    }
}
