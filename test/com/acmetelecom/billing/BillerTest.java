package com.acmetelecom.billing;

import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.billing.Bill;
import com.acmetelecom.billing.BillGenerator;
import com.acmetelecom.billing.Biller;
import com.acmetelecom.billing.strategy.BillingStrategy;
import com.acmetelecom.calling.CallLog;
import com.acmetelecom.calling.FinishedCall;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;
import com.google.common.collect.Lists;

public class BillerTest {

    Mockery context = new JUnit4Mockery() {
	{
	    setImposteriser(ClassImposteriser.INSTANCE);
	    setThreadingPolicy(new Synchroniser());
	}
    };

    private Biller biller;
    private BillGenerator billGenerator;
    private List<Customer> customers;

    private CallLog callLog;
    private TariffLibrary tariffLibrary;
    private CustomerDatabase customerDatabase;
    private BillingStrategy billingStrategy;

    @Before
    public void setUp() {
	billGenerator = context.mock(BillGenerator.class);
	billingStrategy = context.mock(BillingStrategy.class);
	callLog = context.mock(CallLog.class);
	setUpCustomers();

	// Create the biller to test injecting all the dependencies
	biller = new Biller(callLog, tariffLibrary, customerDatabase, billGenerator, billingStrategy);
    }

    private void setUpCustomers() {
	tariffLibrary = context.mock(TariffLibrary.class);
	customerDatabase = context.mock(CustomerDatabase.class);

	customers = new LinkedList<Customer>();
	customers.add(new Customer("customer1", "1", "Business"));
	customers.add(new Customer("customer2", "2", "Leisure"));

	// Each customer is on a different tariff
	context.checking(new Expectations() {
	    {
		this.allowing(tariffLibrary).tarriffFor(this.with(equal(customers.get(0))));
		will(returnValue(Tariff.Business));
		this.allowing(tariffLibrary).tarriffFor(this.with(equal(customers.get(1))));
		will(returnValue(Tariff.Leisure));
	    }
	});
	context.checking(new Expectations() {
	    {
		this.allowing(customerDatabase).getCustomers();
		will(returnValue(customers));
	    }
	});
    }

    private List<FinishedCall> createCalls(final String caller) {
	return Lists.newArrayList(new FinishedCall(caller, "callee", Long.valueOf(0), Long.valueOf(100)));
    }

    @Test
    public void testEachCustomerGetsABill() {
	final List<FinishedCall> calls = createCalls(customers.get(0).getPhoneNumber());
	final List<FinishedCall> calls2 = createCalls(customers.get(1).getPhoneNumber());
	context.checking(new Expectations() {
	    {
		this.allowing(callLog).getCallsForCustomer(customers.get(0).getPhoneNumber());
		will(returnValue(calls));
		this.allowing(callLog).getCallsForCustomer(customers.get(1).getPhoneNumber());
		will(returnValue(calls2));
		this.allowing(callLog).clearCompletedCalls();
	    }
	});

	context.checking(new Expectations() {
	    {
		this.oneOf(billingStrategy).generateBill(customers.get(0), calls, Tariff.Business);
		this.oneOf(billingStrategy).generateBill(customers.get(1), calls2, Tariff.Leisure);
	    }
	});

	context.checking(new Expectations() {
	    {
		this.exactly(2).of(billGenerator).send(with(any(Bill.class)));
	    }
	});

	biller.createCustomerBills();
    }
}
