package com.acmetelecom;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

public class Biller {

    private final CallLog callLog;
    private final TariffLibrary tariffLibrary;
    private final CustomerDatabase customerDatabase;
    private final BillGenerator billGenerator;
    private final BillingStrategy billingStrategy;

    @Autowired
    public Biller(final CallLog callLog, final TariffLibrary tariffLibrary, final CustomerDatabase customerDatabase, final BillGenerator billGenerator,
	    final BillingStrategy billingStrategy) {
	this.callLog = callLog;
	this.tariffLibrary = tariffLibrary;
	this.customerDatabase = customerDatabase;
	this.billGenerator = billGenerator;
	this.billingStrategy = billingStrategy;
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
	final List<FinishedCall> calls = callLog.getCallsForCustomer(customer.getPhoneNumber());
	final Bill bill = billingStrategy.generateBill(customer, calls, tariff);
	billGenerator.send(bill);
    }

}
