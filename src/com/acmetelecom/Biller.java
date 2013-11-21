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

/**
 * Calculates customer bills based on the calls they have made.
 */
public class Biller {

    private final CallLog callLog;
    private final BillGenerator billGenerator;
    private final DaytimePeakPeriod peakPeriod;

    private final TariffLibrary tariffLibrary;
    private final CustomerDatabase customerDatabase;

    /**
     * @param callLog
     *            : represents a list of completed calls for all customers
     * @param tariffLibrary
     *            : holds information about the price tariff for each customer
     * @param customerDatabase
     *            : database with all customers
     * @param billGenerator
     *            : prints bills
     * @param peakPeriod
     *            : specifies the specific billing algorithm based on different
     *            peak/off-peak prices
     */
    @Autowired
    public Biller(final CallLog callLog, final TariffLibrary tariffLibrary, final CustomerDatabase customerDatabase, final BillGenerator billGenerator,
	    final DaytimePeakPeriod peakPeriod) {
	this.callLog = callLog;
	this.tariffLibrary = tariffLibrary;
	this.customerDatabase = customerDatabase;
	this.billGenerator = billGenerator;
	this.peakPeriod = peakPeriod;
    }

    /**
     * Generates bills for all customers in the database.
     */
    public void createCustomerBills() {
	final List<Customer> customers = customerDatabase.getCustomers();
	for (final Customer customer : customers) {
	    createBillFor(customer);
	}
	callLog.clearCompletedCalls();
    }

    /**
     * Calculates the amount a customer has to be charged based on their call
     * history and generates the bill using the inherent BillGenerator.
     */
    private void createBillFor(final Customer customer) {
	// Select the specific tariff for the given customer
	final Tariff tariff = tariffLibrary.tarriffFor(customer);
	// Extract the calls from the call log
	final List<FinishedCall> calls = callLog.getCallsForCustomer(customer.getPhoneNumber());

	BigDecimal totalBill = new BigDecimal(0);
	final List<LineItem> items = new ArrayList<LineItem>();

	// Add up the costs for each call to the total bill
	for (final FinishedCall call : calls) {
	    // Calculate the fraction of call time spend in the peak time
	    // period and calculates the cost for that
	    final int noOfPeakSeconds = peakPeriod.getPeakSeconds(new DateTime(call.startTime()), new DateTime(call.endTime()));
	    final BigDecimal peakCost = new BigDecimal(noOfPeakSeconds).multiply(tariff.peakRate());

	    // Calculate the cost for the off-peak fraction of the call
	    final BigDecimal offPeakCost = new BigDecimal(call.durationSeconds() - noOfPeakSeconds).multiply(tariff.offPeakRate());

	    // Sum up the peak and off-peak rates, rounding up
	    BigDecimal callCost = peakCost.add(offPeakCost);
	    callCost = callCost.setScale(0, RoundingMode.HALF_UP);

	    // Generate the total bill and the line in the final bill
	    totalBill = totalBill.add(callCost);
	    items.add(new LineItem(call, callCost));
	}

	billGenerator.send(customer, items, MoneyFormatter.penceToPounds(totalBill));
    }

}
