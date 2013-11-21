package com.acmetelecom;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class ListCallLog implements CallLog {
    Map<String, List<FinishedCall>> callHistory;

    public ListCallLog() {
	callHistory = Maps.newHashMap();
    }

    @Override
    public void clearCompletedCalls() {
	callHistory.clear();
    }

    @Override
    public void addCall(final FinishedCall c) {
	List<FinishedCall> callerHistory = callHistory.get(c.caller());
	if (callerHistory == null) {
	    callerHistory = new LinkedList<FinishedCall>();
	}
	callerHistory.add(c);
	callHistory.put(c.caller(), callerHistory);
    }

    @Override
    public List<FinishedCall> getCallsForCustomer(final String caller) {
	if (callHistory.containsKey(caller)) {
	    return callHistory.get(caller);
	} else {
	    return new LinkedList<FinishedCall>();
	}
    }

}
