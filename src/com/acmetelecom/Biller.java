package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.BillingSystem.LineItem;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

public class Biller {

    private final CallLog callLog;
    private final TariffLibrary tariffLibrary;
    private final CustomerDatabase customerDatabase;
    private final BillGenerator billGenerator;
    private final DaytimePeakPeriod peakPeriod;

    @Autowired
    public Biller(final CallLog callLog, final TariffLibrary tariffLibrary, final CustomerDatabase customerDatabase, final BillGenerator billGenerator,
	    final DaytimePeakPeriod peakPeriod) {
	this.callLog = callLog;
	this.tariffLibrary = tariffLibrary;
	this.customerDatabase = customerDatabase;
	this.billGenerator = billGenerator;
	this.peakPeriod = peakPeriod;
    }

    public void createCustomerBills() {
	final List<Customer> customers = customerDatabase.getCustomers();
	for (final Customer customer : customers) {
	    createBillFor(customer);
	}
	callLog.clearCompletedCalls();
    }

    private void createBillFor(final Customer customer) {
	final Tariff tariff = tariffLibrary.tarriffFor(customer);

	BigDecimal totalBill = new BigDecimal(0);
	final List<LineItem> items = new ArrayList<LineItem>();

	final List<Call> calls = callLog.getCallsForCustomer(customer.getPhoneNumber());

	for (final Call call : calls) {
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

	billGenerator.send(customer, items, MoneyFormatter.penceToPounds(totalBill));
    }

}
