package com.acmetelecom;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class ListCallLog implements CallLog {
    Map<String, List<Call>> callHistory;

    public ListCallLog() {
	this.callHistory = Maps.newHashMap();
    }

    @Override
    public void clearCompletedCalls() {
	this.callHistory.clear();
    }

    @Override
    public void addCall(final Call c) {
	List<Call> callerHistory = this.callHistory.get(c.caller());
	if (callerHistory == null) {
	    callerHistory = new LinkedList<Call>();
	}
	callerHistory.add(c);
	this.callHistory.put(c.caller(), callerHistory);
    }

    @Override
    public Iterable<Call> getCallsForCustomer(final String caller) {
	if (this.callHistory.containsKey(caller)) {
	    return this.callHistory.get(caller);
	} else {
	    return new LinkedList<Call>();
	}
    }
}
