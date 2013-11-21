package com.acmetelecom;

import java.util.List;

public interface CallLog {
    public void addCall(final FinishedCall c);

    public void clearCompletedCalls();

    public List<FinishedCall> getCallsForCustomer(String caller);
}
