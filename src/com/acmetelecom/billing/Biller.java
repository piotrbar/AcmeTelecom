package com.acmetelecom.billing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.billing.strategy.BillingStrategy;
import com.acmetelecom.calling.CallLog;
import com.acmetelecom.calling.FinishedCall;
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
    private final BillingStrategy billingStrategy;

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
	    final BillingStrategy billingStrategy) {
	this.callLog = callLog;
	this.tariffLibrary = tariffLibrary;
	this.customerDatabase = customerDatabase;
	this.billGenerator = billGenerator;
	this.billingStrategy = billingStrategy;
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
	final List<FinishedCall> calls = callLog.getCallsForCustomer(customer.getPhoneNumber());
	final Bill bill = billingStrategy.generateBill(customer, calls, tariff);
	billGenerator.send(bill);
    }

}
