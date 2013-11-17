package com.acmetelecom;


public interface CallLog {
    public void addCall(final Call c);

    public void clearCompletedCalls();

    public Iterable<Call> getCallsForCustomer(String caller);
}
