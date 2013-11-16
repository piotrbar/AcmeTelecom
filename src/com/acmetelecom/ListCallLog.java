package com.acmetelecom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListCallLog implements CallLog {
    Map<String, List<Call>> callHistory;

    public ListCallLog() {
	callHistory = new HashMap<String, List<Call>>();
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
    public List<Call> getCallsForCustomer(final String caller) {
	return callHistory.get(caller);
    }
}
