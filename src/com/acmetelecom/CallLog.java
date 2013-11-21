package com.acmetelecom;

import java.util.List;

/**
 * A call log is a container for FinishedCall objects.
 */
public interface CallLog {

    /**
     * Adds the given call to the call log.
     */
    public void addCall(final FinishedCall c);

    /**
     * Clears the call log.
     */
    public void clearCompletedCalls();

    /**
     * Gets all completed calls for a given customer.
     */
    public List<FinishedCall> getCallsForCustomer(String caller);
}
