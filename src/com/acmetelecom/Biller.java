package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.acmetelecom.BillingSystem.LineItem;
import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

public class Biller {
    private final CallLog callLog;
    private final TariffLibrary tariffLibrary;
    private final CustomerDatabase customerDatabase;
    private final BillGenerator billGenerator;

    public Biller(final CallLog callLog, final BillGenerator billGenerator) {
	this.callLog = callLog;
	this.billGenerator = billGenerator;
	this.tariffLibrary = CentralTariffDatabase.getInstance();
	this.customerDatabase = CentralCustomerDatabase.getInstance();
    }

    public Biller(final CallLog callLog, final TariffLibrary tariffLibrary, final CustomerDatabase customerDatabase, final BillGenerator billGenerator) {
	this.callLog = callLog;
	this.tariffLibrary = tariffLibrary;
	this.customerDatabase = customerDatabase;
	this.billGenerator = billGenerator;
    }

    public void createCustomerBills() {
	final List<Customer> customers = this.customerDatabase.getCustomers();
	for (final Customer customer : customers) {
	    this.createBillFor(customer);
	}
	this.callLog.clearCompletedCalls();
    }

    private void createBillFor(final Customer customer) {
	final Iterable<Call> calls = this.callLog.getCallsForCustomer(customer.getPhoneNumber());

	BigDecimal totalBill = new BigDecimal(0);
	final List<LineItem> items = new ArrayList<LineItem>();

	for (final Call call : calls) {

	    final Tariff tariff = this.tariffLibrary.tarriffFor(customer);

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

	this.billGenerator.send(customer, items, MoneyFormatter.penceToPounds(totalBill));
    }
}
