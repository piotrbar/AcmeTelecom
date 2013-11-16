package com.acmetelecom;

import java.util.List;

public interface CallLog {
    public void addCall(final Call c);

    public void clearCompletedCalls();

    public List<Call> getCalls(String caller);
}
