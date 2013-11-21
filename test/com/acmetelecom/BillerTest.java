package com.acmetelecom;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
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
	    setImposteriser(ClassImposteriser.INSTANCE);
	    setThreadingPolicy(new Synchroniser());
	}
    };

    // Peak time period
    private final int peakStart = 7;
    private final int peakEnd = 19;

    private Biller biller;
    private BillGenerator billGenerator;
    private List<Customer> customers;

    private CallLog callLog;

    private TariffLibrary tariffLibrary;

    @Before
    public void setUp() {
	tariffLibrary = context.mock(TariffLibrary.class);

	// Mock the customer database with 3 customers
	final CustomerDatabase customerDatabase = context.mock(CustomerDatabase.class);
	customers = new LinkedList<Customer>();
	customers.add(context.mock(Customer.class, "customer1"));
	context.checking(new Expectations() {
	    {
		this.allowing(customers.get(0)).getFullName();
		will(returnValue("cutomer1"));
		this.allowing(customers.get(0)).getPhoneNumber();
		will(returnValue("123456789"));
		this.allowing(customers.get(0)).getPricePlan();
		will(returnValue("ripoff"));
	    }
	});
	customers.add(context.mock(Customer.class, "customer2"));
	context.checking(new Expectations() {
	    {
		this.allowing(customers.get(1)).getFullName();
		will(returnValue("cutomer2"));
		this.allowing(customers.get(1)).getPhoneNumber();
		will(returnValue("123085684"));
		this.allowing(customers.get(1)).getPricePlan();
		will(returnValue("ripoff2"));
	    }
	});
	customers.add(context.mock(Customer.class, "customer3"));
	context.checking(new Expectations() {
	    {
		this.allowing(customers.get(2)).getFullName();
		will(returnValue("cutomer3"));
		this.allowing(customers.get(2)).getPhoneNumber();
		will(returnValue("12324536754"));
		this.allowing(customers.get(2)).getPricePlan();
		will(returnValue("ripoff3"));
	    }
	});
	customers.add(context.mock(Customer.class, "customer4"));
	context.checking(new Expectations() {
	    {
		this.allowing(customers.get(3)).getFullName();
		will(returnValue("cutomer4"));
		this.allowing(customers.get(3)).getPhoneNumber();
		will(returnValue("12327384759"));
		this.allowing(customers.get(3)).getPricePlan();
		will(returnValue("ripoff"));
	    }
	});
	// Each customer is on a different tariff
	context.checking(new Expectations() {
	    {
		this.allowing(tariffLibrary).tarriffFor(this.with(equal(customers.get(0))));
		will(returnValue(Tariff.Business));
		this.allowing(tariffLibrary).tarriffFor(this.with(equal(customers.get(1))));
		will(returnValue(Tariff.Leisure));
		this.allowing(tariffLibrary).tarriffFor(this.with(equal(customers.get(2))));
		will(returnValue(Tariff.Standard));
		this.allowing(tariffLibrary).tarriffFor(this.with(equal(customers.get(3))));
		will(returnValue(Tariff.Business));
	    }
	});
	context.checking(new Expectations() {
	    {
		this.allowing(customerDatabase).getCustomers();
		will(returnValue(customers));
	    }
	});

	callLog = new ListCallLog();

	// Mock the bill generator, we don't want to be actually sending the
	// bills to anybody
	billGenerator = context.mock(BillGenerator.class);

	// Create the biller to test injecting all the dependencies

	biller = new Biller(callLog, tariffLibrary, customerDatabase, billGenerator, new DaytimePeakPeriod(peakStart, peakEnd));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEachCustomerGetsABill() {
	context.checking(new Expectations() {
	    {
		this.oneOf(billGenerator).send(this.with(equal(customers.get(0))), this.with(any(List.class)), this.with(any(String.class)));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(1))), this.with(any(List.class)), this.with(any(String.class)));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(2))), this.with(any(List.class)), this.with(any(String.class)));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(3))), this.with(any(List.class)), this.with(any(String.class)));
	    }
	});

	biller.createCustomerBills();
    }

    @Test
    public void testDifferentCustomersAreChargedAccordingToDifferentTarrifs() {

	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

	// Each customer calls their mother for an hour in offpeak time
	// The default peak time is between 7 and 19
	final long hourLength = 1000 * 60 * 60;
	long timeStart = hourLength * 3; // 3am
	for (final Customer customer : customers) {
	    callLog.addCall(new Call(customer.getPhoneNumber(), "mother", timeStart, timeStart + hourLength));
	}
	final BigDecimal hourInSeconds = new BigDecimal(60 * 60);
	context.checking(new Expectations() {
	    {
		this.oneOf(billGenerator).send(this.with(equal(customers.get(0))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.offPeakRate().multiply(hourInSeconds)))));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(1))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Leisure.offPeakRate().multiply(hourInSeconds)))));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(2))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Standard.offPeakRate().multiply(hourInSeconds)))));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(3))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.offPeakRate().multiply(hourInSeconds)))));

	    }
	});

	biller.createCustomerBills();
	callLog.clearCompletedCalls();

	// Each customer calls their mother for an hour in peak time
	// The default peak time is between 7 and 19
	timeStart = hourLength * 15; // 3pm
	for (final Customer customer : customers) {
	    callLog.addCall(new Call(customer.getPhoneNumber(), "mother", timeStart, timeStart + hourLength));
	}
	context.checking(new Expectations() {
	    {
		this.oneOf(billGenerator).send(this.with(equal(customers.get(0))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.peakRate().multiply(hourInSeconds)))));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(1))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Leisure.peakRate().multiply(hourInSeconds)))));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(2))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Standard.peakRate().multiply(hourInSeconds)))));
		this.oneOf(billGenerator).send(this.with(equal(customers.get(3))), this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.peakRate().multiply(hourInSeconds)))));

	    }
	});
    }

    @Test
    public void testCorrectTotalSpanningPeakAndOffPeak() {

	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

	// Each customer calls their mother
	// The default peak time is between 7 and 19
	final long hourLength = 1000 * 60 * 60;
	// 6am to 8am = 2hr long
	callLog.addCall(new Call(customers.get(0).getPhoneNumber(), "mother", hourLength * 6, hourLength * 8));
	// 6pm to 8pm = 2hr long
	callLog.addCall(new Call(customers.get(1).getPhoneNumber(), "mother", hourLength * 18, hourLength * 20));
	// 6am to 8pm = 14hr long
	callLog.addCall(new Call(customers.get(2).getPhoneNumber(), "mother", hourLength * 6, hourLength * 20));
	// 6am to 6am (3 days)
	callLog.addCall(new Call(customers.get(3).getPhoneNumber(), "mother", hourLength * 6, hourLength * 56));

	final BigDecimal oneHoursInSeconds = new BigDecimal(1 * 60 * 60);
	final BigDecimal twoHoursInSeconds = new BigDecimal(2 * 60 * 60);
	final BigDecimal twelveHoursInSeconds = new BigDecimal(12 * 60 * 60);
	final BigDecimal twentyFiveHoursInSeconds = new BigDecimal(25 * 60 * 60);

	context.checking(new Expectations() {
	    {
		this.oneOf(billGenerator).send(
			this.with(equal(customers.get(0))),
			this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.peakRate().multiply(oneHoursInSeconds)
				.add(Tariff.Business.offPeakRate().multiply(oneHoursInSeconds))))));
		this.oneOf(billGenerator).send(
			this.with(equal(customers.get(1))),
			this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Leisure.peakRate().multiply(oneHoursInSeconds)
				.add(Tariff.Leisure.offPeakRate().multiply(oneHoursInSeconds))))));
		this.oneOf(billGenerator).send(
			this.with(equal(customers.get(2))),
			this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Standard.peakRate().multiply(twelveHoursInSeconds)
				.add(Tariff.Standard.offPeakRate().multiply(twoHoursInSeconds))))));
		this.oneOf(billGenerator).send(
			this.with(equal(customers.get(3))),
			this.with(any(List.class)),
			this.with(equal(MoneyFormatter.penceToPounds(Tariff.Business.peakRate().multiply(twentyFiveHoursInSeconds)
				.add(Tariff.Business.offPeakRate().multiply(twentyFiveHoursInSeconds))))));
	    }
	});

	biller.createCustomerBills();
    }
}
