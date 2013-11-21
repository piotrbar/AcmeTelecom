package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.google.common.collect.Lists;

public class FairBillingStrategyTest {

    Mockery context = new JUnit4Mockery() {
	{
	    setImposteriser(ClassImposteriser.INSTANCE);
	    setThreadingPolicy(new Synchroniser());
	}
    };

    private static final int MILLIS_PER_HOUR = 3600000;
    private static final int SECONDS_PER_HOUR = 3600;

    private BillingStrategy billingStrategy;
    private DaytimePeakPeriod peakPeriod;
    private List<Customer> customers;

    @Before
    public void setUp() {
	peakPeriod = context.mock(DaytimePeakPeriod.class);
	setUpCustomers();

	billingStrategy = new FairBillingStrategy(peakPeriod);
    }

    private void setUpCustomers() {
	customers = new LinkedList<Customer>();
	customers.add(new Customer("customer1", "1", "Business"));
	customers.add(new Customer("customer2", "2", "Leisure"));
	customers.add(new Customer("customer3", "3", "Standard"));
    }

    private List<FinishedCall> createCalls(final String caller, final long start, final long finish) {
	return Lists.newArrayList(new FinishedCall(caller, "callee", Long.valueOf(start), Long.valueOf(finish)));
    }

    @Test
    public void testDifferentCustomersAreChargedAccordingToDifferentTarrifs() {
	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	List<FinishedCall> calls = createCalls(customers.get(0).getPhoneNumber(), 0, MILLIS_PER_HOUR);
	List<FinishedCall> calls2 = createCalls(customers.get(1).getPhoneNumber(), 0, MILLIS_PER_HOUR);
	List<FinishedCall> calls3 = createCalls(customers.get(2).getPhoneNumber(), 0, MILLIS_PER_HOUR);

	context.checking(new Expectations() {
	    {
		exactly(3).of(peakPeriod).getPeakSeconds(with(any(DateTime.class)), with(any(DateTime.class)));
		will(returnValue(SECONDS_PER_HOUR));
	    }
	});

	final BigDecimal oneHour = new BigDecimal(SECONDS_PER_HOUR);

	final Bill bill = billingStrategy.generateBill(customers.get(0), calls, Tariff.Business);
	final BigDecimal expectedTotal = Tariff.Business.peakRate().multiply(oneHour);
	Assert.assertEquals(expectedTotal.setScale(0, RoundingMode.HALF_UP), bill.getTotal());

	final Bill bill2 = billingStrategy.generateBill(customers.get(1), calls2, Tariff.Leisure);
	final BigDecimal expectedTotal2 = Tariff.Leisure.peakRate().multiply(oneHour);
	Assert.assertEquals(expectedTotal2.setScale(0, RoundingMode.HALF_UP), bill2.getTotal());

	final Bill bill3 = billingStrategy.generateBill(customers.get(2), calls3, Tariff.Standard);
	final BigDecimal expectedTotal3 = Tariff.Standard.peakRate().multiply(oneHour);
	Assert.assertEquals(expectedTotal3.setScale(0, RoundingMode.HALF_UP), bill3.getTotal());
    }

    @Test
    public void testCorrectTotalSpanningPeakAndOffPeak() {
	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	List<FinishedCall> calls = createCalls(customers.get(0).getPhoneNumber(), 0, MILLIS_PER_HOUR);

	// Let's say half of the call was in the peak time and half od the call
	// in offpeak time
	final BigDecimal halfAnHour = new BigDecimal(SECONDS_PER_HOUR / 2);

	context.checking(new Expectations() {
	    {
		oneOf(peakPeriod).getPeakSeconds(with(any(DateTime.class)), with(any(DateTime.class)));
		will(returnValue(SECONDS_PER_HOUR / 2));
	    }
	});

	final Bill bill = billingStrategy.generateBill(customers.get(0), calls, Tariff.Business);
	final BigDecimal expectedTotal = Tariff.Business.peakRate().multiply(halfAnHour).add(Tariff.Business.offPeakRate().multiply(halfAnHour));
	Assert.assertEquals(expectedTotal.setScale(0, RoundingMode.HALF_UP), bill.getTotal());
    }
}
