package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.acmetelecom.BillingSystem.LineItem;
import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;

public class Biller {
    private final CallLog callLog;

    public Biller(final CallLog callLog) {
	this.callLog = callLog;
    }

    public void createCustomerBills() {
	final List<Customer> customers = CentralCustomerDatabase.getInstance().getCustomers();
	for (final Customer customer : customers) {
	    createBillFor(customer);
	}
	callLog.clearCompletedCalls();
    }

    private void createBillFor(final Customer customer) {
	// final List<CallEvent> customerEvents = new ArrayList<CallEvent>();
	// for (final CallEvent callEvent : callLog.getCallEvents()) {
	// if (callEvent.getCaller().equals(customer.getPhoneNumber())) {
	// customerEvents.add(callEvent);
	// }
	// }
	//
	// final List<Call> calls = new ArrayList<Call>();
	//
	// CallEvent start = null;
	// for (final CallEvent event : customerEvents) {
	// if (event instanceof CallStart) {
	// start = event;
	// }
	// if (event instanceof CallEnd && start != null) {
	// calls.add(new Call(start, event));
	// start = null;
	// }
	// }

	final List<Call> calls = callLog.getCalls(customer.getPhoneNumber());

	BigDecimal totalBill = new BigDecimal(0);
	final List<LineItem> items = new ArrayList<LineItem>();

	for (final Call call : calls) {

	    final Tariff tariff = CentralTariffDatabase.getInstance().tarriffFor(customer);

	    BigDecimal cost;

	    final DaytimePeakPeriod peakPeriod = new DaytimePeakPeriod();
	    if (peakPeriod.offPeak(call.startTime()) && peakPeriod.offPeak(call.endTime()) && call.durationSeconds() < 12 * 60 * 60) {
		cost = new BigDecimal(call.durationSeconds()).multiply(tariff.offPeakRate());
	    } else {
		cost = new BigDecimal(call.durationSeconds()).multiply(tariff.peakRate());
	    }

	    cost = cost.setScale(0, RoundingMode.HALF_UP);
	    final BigDecimal callCost = cost;
	    totalBill = totalBill.add(callCost);
	    items.add(new LineItem(call, callCost));
	}

	new BillGenerator().send(customer, items, MoneyFormatter.penceToPounds(totalBill));
    }
}
