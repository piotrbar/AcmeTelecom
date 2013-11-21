package com.acmetelecom.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acmetelecom.billing.BillGenerator;
import com.acmetelecom.billing.LineItem;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.util.MoneyFormatter;
import com.acmetelecom.util.Printer;

@RunWith(JMock.class)
public class BillGeneratorTest {

    Mockery context = new JUnit4Mockery() {
	{
	    setImposteriser(ClassImposteriser.INSTANCE);
	    setThreadingPolicy(new Synchroniser());
	}
    };

    // The printer mock used for sensing
    Printer printer = context.mock(Printer.class);

    // Without the documentation we cannot directly instantiate the Customer,
    // hence lets mock it.
    String customerName = "Stan";
    String customerNumber = "+44781234234";
    String customerPlan = "rip-off plan";
    Customer customer = context.mock(Customer.class);

    // Details for the three calls
    String time = "morning";
    String callee = "robert";
    String duration = "duration";
    BigDecimal cost = new BigDecimal(100);
    List<LineItem> calls = new ArrayList<LineItem>(3);

    // Bill total
    String totalBill = "300 dollars";

    @Before
    public void setUp() {
	// Set up the calls for including in the bill
	final LineItem call = context.mock(LineItem.class);
	context.checking(new Expectations() {
	    {
		allowing(call).date();
		will(returnValue(time));
		allowing(call).callee();
		will(returnValue(callee));
		allowing(call).durationMinutes();
		will(returnValue(duration));
		allowing(call).cost();
		will(returnValue(cost));
	    }
	});
	for (int i = 0; i < 3; ++i) {
	    calls.add(call);
	}

	// Set up the customer details
	context.checking(new Expectations() {
	    {
		allowing(customer).getFullName();
		will(returnValue(customerName));
		allowing(customer).getPhoneNumber();
		will(returnValue(customerNumber));
		allowing(customer).getPricePlan();
		will(returnValue(customerPlan));
	    }
	});
    }

    @Test
    public void testSend() {

	// Testing the BillGenerator with a mock printer
	final BillGenerator billGenerator = new BillGenerator(printer);

	final Sequence printSequence = context.sequence("printSequence");
	// Make sure everything gets printed
	context.checking(new Expectations() {
	    {
		oneOf(printer).printHeading(with(equal(customerName)), with(equal(customerNumber)), with(equal(customerPlan)));
		inSequence(printSequence);
		exactly(calls.size()).of(printer).printItem(with(equal(time)), with(equal(callee)), with(equal(duration)),
			with(equal(MoneyFormatter.penceToPounds(cost))));
		inSequence(printSequence);
		oneOf(printer).printTotal(with(equal(totalBill)));
		inSequence(printSequence);
	    }
	});

	billGenerator.send(customer, calls, totalBill);
    }

    @After
    public void tearDown() {
	// Nothing to clean
    }
}
