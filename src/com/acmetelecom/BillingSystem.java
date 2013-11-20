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
	this.callTracker.callInitiated(caller, callee);
    }

    public void callCompleted(final String caller, final String callee) {
	this.callTracker.callCompleted(caller, callee);
    }

    public void createCustomerBills() {
	this.biller.createCustomerBills();
    }

    static class LineItem {
	private final Call call;
	private final BigDecimal callCost;

	public LineItem(final Call call, final BigDecimal callCost) {
	    this.call = call;
	    this.callCost = callCost;
	}

	public String date() {
	    return this.call.date();
	}

	public String callee() {
	    return this.call.callee();
	}

	// TODO Shall we abstract out the Formatter?
	public String durationMinutes() {
	    return "" + this.call.durationSeconds() / 60 + ":" + String.format("%02d", this.call.durationSeconds() % 60);
	}

	public BigDecimal cost() {
	    return this.callCost;
	}
    }

}
