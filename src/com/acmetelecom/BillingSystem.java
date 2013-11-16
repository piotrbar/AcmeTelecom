package com.acmetelecom;

import java.math.BigDecimal;

public class BillingSystem {
    private final Biller biller;
    private final CallTracker callTracker;

    public BillingSystem(final Biller biller, final CallTracker callTracker) {
	this.biller = biller;
	this.callTracker = callTracker;
    }

    public void callInitiated(final String caller, final String callee) {
	callTracker.callInitiated(caller, callee);
    }

    public void callCompleted(final String caller, final String callee) {
	callTracker.callCompleted(caller, callee);
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
