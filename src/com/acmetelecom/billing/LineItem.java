package com.acmetelecom.billing;

import java.math.BigDecimal;

import com.acmetelecom.calling.FinishedCall;

public class LineItem {
    private final FinishedCall call;
    private final BigDecimal callCost;

    public LineItem(final FinishedCall call, final BigDecimal callCost) {
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