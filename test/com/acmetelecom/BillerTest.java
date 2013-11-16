package com.acmetelecom;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.TariffLibrary;

public class BillerTest {

    Mockery context = new JUnit4Mockery() {
	{
	    this.setImposteriser(ClassImposteriser.INSTANCE);
	}
    };

    private Biller biller;

    @Before
    public void setUp() {

	final CallLog callLog = new ListCallLog();
	final TariffLibrary tariffLibrary = this.context.mock(TariffLibrary.class);
	final CustomerDatabase customerDatabase = this.context.mock(CustomerDatabase.class);
	final Printer printer = this.context.mock(Printer.class);
	final BillGenerator billGenerator = new BillGenerator(printer);

	this.biller = new Biller(callLog, tariffLibrary, customerDatabase, billGenerator);

	this.context.checking(new Expectations() {
	    {
	    }
	});
    }

    @Test
    public void testEachCustomerGetsABill() {

    }

    @Test
    public void testDifferentCustomersAreChargedAccordingToDifferentTarrifs() {

    }

    @Test
    public void testCorrectTotalSpanningPeakAndOffPeak() {

    }
}
