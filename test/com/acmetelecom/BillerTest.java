package com.acmetelecom;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.TariffLibrary;

public class BillerTest {

    Mockery context = new JUnit4Mockery() {
	{
	    this.setImposteriser(ClassImposteriser.INSTANCE);
	}
    };

    private Biller biller;
    private BillGenerator billGenerator;
    private List<Customer> customers;

    @Before
    public void setUp() {
	// Mock the tariff database
	final TariffLibrary tariffLibrary = this.context.mock(TariffLibrary.class);

	// Mock the customer database with 3 customers
	final CustomerDatabase customerDatabase = this.context.mock(CustomerDatabase.class);
	this.customers = new LinkedList<Customer>();
	this.customers.add(this.context.mock(Customer.class, "customer1"));
	this.context.checking(new Expectations() {
	    {
		this.allowing(BillerTest.this.customers.get(0)).getFullName();
		this.will(returnValue("cutomer1"));
		this.allowing(BillerTest.this.customers.get(0)).getPhoneNumber();
		this.will(returnValue("123456789"));
		this.allowing(BillerTest.this.customers.get(0)).getPricePlan();
		this.will(returnValue("ripoff"));
	    }
	});
	this.customers.add(this.context.mock(Customer.class, "customer2"));
	this.context.checking(new Expectations() {
	    {
		this.allowing(BillerTest.this.customers.get(1)).getFullName();
		this.will(returnValue("cutomer2"));
		this.allowing(BillerTest.this.customers.get(1)).getPhoneNumber();
		this.will(returnValue("123085684"));
		this.allowing(BillerTest.this.customers.get(1)).getPricePlan();
		this.will(returnValue("ripoff2"));
	    }
	});
	this.customers.add(this.context.mock(Customer.class, "customer3"));
	this.context.checking(new Expectations() {
	    {
		this.allowing(BillerTest.this.customers.get(2)).getFullName();
		this.will(returnValue("cutomer3"));
		this.allowing(BillerTest.this.customers.get(2)).getPhoneNumber();
		this.will(returnValue("12324536754"));
		this.allowing(BillerTest.this.customers.get(2)).getPricePlan();
		this.will(returnValue("ripoff3"));
	    }
	});
	this.context.checking(new Expectations() {
	    {
		this.allowing(customerDatabase).getCustomers();
		this.will(returnValue(BillerTest.this.customers));
	    }
	});

	// Create a new call-log, no need to mock this simple class
	final CallLog callLog = new ListCallLog();

	// Mock the bill generator, we don't want to be actually sending the
	// bills to anybody
	this.billGenerator = this.context.mock(BillGenerator.class);

	// Create the biller to test injecting all the dependencies
	this.biller = new Biller(callLog, tariffLibrary, customerDatabase, this.billGenerator);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEachCustomerGetsABill() {
	this.context.checking(new Expectations() {
	    {
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(0))), this.with(any(List.class)),
			this.with(any(String.class)));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(1))), this.with(any(List.class)),
			this.with(any(String.class)));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(2))), this.with(any(List.class)),
			this.with(any(String.class)));
	    }
	});

	this.biller.createCustomerBills();
    }

    @Test
    public void testDifferentCustomersAreChargedAccordingToDifferentTarrifs() {
	fail("Not implemented");
    }

    @Test
    public void testCorrectTotalSpanningPeakAndOffPeak() {
	fail("Not implemented");
    }
}
