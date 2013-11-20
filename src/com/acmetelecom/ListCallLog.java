package com.acmetelecom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListCallLog implements CallLog {

    private final Map<String, List<Call>> callHistory;

    public ListCallLog() {
	this.callHistory = new HashMap<String, List<Call>>();
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
    public List<Call> getCalls(final String caller) {
	return this.callHistory.get(caller);
    }

}
