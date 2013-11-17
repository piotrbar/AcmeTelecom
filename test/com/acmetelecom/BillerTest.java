package com.acmetelecom;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
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

    private CallLog callLog;

    private TariffLibrary tariffLibrary;

    @Before
    public void setUp() {
	this.tariffLibrary = this.context.mock(TariffLibrary.class);

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

	this.callLog = new ListCallLog();

	// Mock the bill generator, we don't want to be actually sending the
	// bills to anybody
	this.billGenerator = this.context.mock(BillGenerator.class);

	// Create the biller to test injecting all the dependencies
	this.biller = new Biller(this.callLog, this.tariffLibrary, customerDatabase, this.billGenerator);

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
	// Each customer is on a different tariff
	this.context.checking(new Expectations() {
	    {
		this.allowing(BillerTest.this.tariffLibrary).tarriffFor(this.with(equal(BillerTest.this.customers.get(0))));
		this.will(returnValue(Tariff.Business));
		this.allowing(BillerTest.this.tariffLibrary).tarriffFor(this.with(equal(BillerTest.this.customers.get(1))));
		this.will(returnValue(Tariff.Leisure));
		this.allowing(BillerTest.this.tariffLibrary).tarriffFor(this.with(equal(BillerTest.this.customers.get(2))));
		this.will(returnValue(Tariff.Standard));
	    }
	});

	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

	// Each customer calls their mother for an hour in offpeak time
	// The default peak time is between 7 and 19
	final long hourLength = 1000 * 60 * 60;
	long timeStart = hourLength * 3; // 3am
	for (final Customer customer : this.customers) {
	    this.callLog.addCall(new Call(customer.getPhoneNumber(), "mother", timeStart, timeStart + hourLength));
	}
	final BigDecimal hourInSeconds = new BigDecimal(60 * 60);
	this.context.checking(new Expectations() {
	    {
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(0))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.offPeakRate().multiply(hourInSeconds)))));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(1))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Leisure.offPeakRate().multiply(hourInSeconds)))));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(2))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Standard.offPeakRate().multiply(hourInSeconds)))));
	    }
	});

	this.biller.createCustomerBills();
	this.callLog.clearCompletedCalls();

	// Each customer calls their mother for an hour in peak time
	// The default peak time is between 7 and 19
	timeStart = hourLength * 15; // 3pm
	for (final Customer customer : this.customers) {
	    this.callLog.addCall(new Call(customer.getPhoneNumber(), "mother", timeStart, timeStart + hourLength));
	}
	this.context.checking(new Expectations() {
	    {
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(0))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.peakRate().multiply(hourInSeconds)))));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(1))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Leisure.peakRate().multiply(hourInSeconds)))));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(2))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Standard.peakRate().multiply(hourInSeconds)))));
	    }
	});
    }

    @Test
    public void testCorrectTotalSpanningPeakAndOffPeak() {
	// Each customer is on a different tariff
	this.context.checking(new Expectations() {
	    {
		this.allowing(BillerTest.this.tariffLibrary).tarriffFor(this.with(equal(BillerTest.this.customers.get(0))));
		this.will(returnValue(Tariff.Business));
		this.allowing(BillerTest.this.tariffLibrary).tarriffFor(this.with(equal(BillerTest.this.customers.get(1))));
		this.will(returnValue(Tariff.Leisure));
		this.allowing(BillerTest.this.tariffLibrary).tarriffFor(this.with(equal(BillerTest.this.customers.get(2))));
		this.will(returnValue(Tariff.Standard));
	    }
	});

	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

	// Each customer calls their mother
	// The default peak time is between 7 and 19
	final long hourLength = 1000 * 60 * 60;
	// 6am to 8am = 2hr long
	this.callLog.addCall(new Call(this.customers.get(0).getPhoneNumber(), "mother", hourLength * 6, hourLength * 8));
	// 6pm to 8pm = 2hr long
	this.callLog.addCall(new Call(this.customers.get(1).getPhoneNumber(), "mother", hourLength * 18, hourLength * 20));
	// 6am to 8pm = 14hr long
	this.callLog.addCall(new Call(this.customers.get(2).getPhoneNumber(), "mother", hourLength * 6, hourLength * 20));

	final BigDecimal twoHoursInSeconds = new BigDecimal(2 * 60 * 60);
	final BigDecimal fourteenHoursInSeconds = new BigDecimal(14 * 60 * 60);
	this.context.checking(new Expectations() {
	    {
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(0))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.peakRate().multiply(twoHoursInSeconds)))));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(1))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Leisure.peakRate().multiply(twoHoursInSeconds)))));
		this.oneOf(BillerTest.this.billGenerator).send(this.with(equal(BillerTest.this.customers.get(2))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Standard.peakRate().multiply(fourteenHoursInSeconds)))));
	    }
	});

	this.biller.createCustomerBills();
    }
}
