package com.acmetelecom;

import java.util.List;

public interface CallLog {
    public void callInitiated(String caller, String callee);

    public void callCompleted(String caller, String callee);

    public void clearCompletedCalls();

    public List<CallEvent> getCallEvents();
}
