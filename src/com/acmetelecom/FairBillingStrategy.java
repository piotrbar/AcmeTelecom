package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.BillingSystem.LineItem;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.google.common.collect.Lists;

public class FairBillingStrategy implements BillingStrategy {

    private final DaytimePeakPeriod peakPeriod;

    @Autowired
    public FairBillingStrategy(final DaytimePeakPeriod peakPeriod) {
	this.peakPeriod = peakPeriod;
    }

    @Override
    public Bill generateBill(final Customer customer, final Iterable<FinishedCall> calls, final Tariff tariff) {
	final List<LineItem> items = Lists.newArrayList();
	BigDecimal totalBill = new BigDecimal(0);
	for (final FinishedCall call : calls) {
	    // Compute the current call cost
	    final int noOfPeakSeconds = peakPeriod.getPeakSeconds(new DateTime(call.startTime()), new DateTime(call.endTime()));

	    final BigDecimal peakCost = new BigDecimal(noOfPeakSeconds).multiply(tariff.peakRate());
	    final BigDecimal offPeakCost = new BigDecimal(call.durationSeconds() - noOfPeakSeconds).multiply(tariff.offPeakRate());

	    BigDecimal callCost = peakCost.add(offPeakCost);
	    callCost = callCost.setScale(0, RoundingMode.HALF_UP);

	    // Generate the total bill and the line in the final bill
	    totalBill = totalBill.add(callCost);
	    items.add(new LineItem(call, callCost));
	}
	return new Bill(customer, items, totalBill);
    }
}
