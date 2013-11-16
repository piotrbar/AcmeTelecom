package com.acmetelecom;

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

    private CustomerDatabase customerDatabase;

    private BillGenerator billGenerator;

    private List<Customer> customer;

    @Before
    public void setUp() {

	final CallLog callLog = new ListCallLog();
	final TariffLibrary tariffLibrary = this.context.mock(TariffLibrary.class);
	this.customerDatabase = this.context.mock(CustomerDatabase.class);
	this.customer = new LinkedList<Customer>();
	this.customer.add(this.context.mock(Customer.class, "customer1"));
	this.customer.add(this.context.mock(Customer.class, "customer2"));
	this.customer.add(this.context.mock(Customer.class, "customer3"));

	this.billGenerator = this.context.mock(BillGenerator.class);

	this.biller = new Biller(callLog, tariffLibrary, this.customerDatabase, this.billGenerator);

	this.context.checking(new Expectations() {
	    {
		this.allowing(BillerTest.this.customerDatabase).getCustomers();
		this.will(returnEnumeration(BillerTest.this.customer));
	    }
	});
    }

    @Test
    public void testEachCustomerGetsABill() {
	this.context.checking(new Expectations() {
	    {
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customer.get(0))), this.with(any(List.class)),
			this.with(any(String.class)));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customer.get(1))), this.with(any(List.class)),
			this.with(any(String.class)));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customer.get(2))), this.with(any(List.class)),
			this.with(any(String.class)));
	    }
	});
    }

    @Test
    public void testDifferentCustomersAreChargedAccordingToDifferentTarrifs() {

    }

    @Test
    public void testCorrectTotalSpanningPeakAndOffPeak() {

    }
}
