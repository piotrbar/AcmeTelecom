package com.acmetelecom;

import java.math.BigDecimal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.exceptions.IllegalCallException;

public class BillingSystem {

    private final Biller biller;
    private final CallTracker callTracker;

    private static final Logger LOG = LogManager.getLogger(BillingSystem.class);

    @Autowired
    public BillingSystem(final Biller biller, final CallTracker callTracker) {
	this.biller = biller;
	this.callTracker = callTracker;
    }

    public void callInitiated(final String caller, final String callee) {
	try {
	    callTracker.callInitiated(caller, callee);
	}
	catch (final IllegalCallException e) {
	    LOG.error(String.format("Call between %s and %s could not have been initated", caller, callee), e.getCause());
	}
    }

    public void callCompleted(final String caller, final String callee) {
	try {
	    callTracker.callCompleted(caller, callee);
	}
	catch (final IllegalCallException e) {
	    LOG.error(String.format("Call between %s and %s could not have been completed", caller, callee), e.getCause());
	}
    }

    public void createCustomerBills() {
	biller.createCustomerBills();
    }

    static class LineItem {
	private final Call call;
	private final BigDecimal callCost;

	public LineItem(final Call call, final BigDecimal callCost) {
	    this.call = call;
	    this.callCost = callCost;
	}

	public String date() {
	    return call.date();
	}

	public String callee() {
	    return call.callee();
	}

	// TODO Shall we abstract out the Formatter?
	public String durationMinutes() {
	    return "" + call.durationSeconds() / 60 + ":" + String.format("%02d", call.durationSeconds() % 60);
	}

	public BigDecimal cost() {
	    return callCost;
	}
    }
}
