package com.acmetelecom;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.billing.Biller;
import com.acmetelecom.calling.CallTracker;
import com.acmetelecom.exceptions.IllegalCallException;

/**
 * Billing system is the main entry point to the system. It's main purpose is
 * bill generation for all customers in a database.
 * 
 * For legacy reasons it is also used for call registration. Internally, this is
 * delegated to the CallTracker. Consumer packages can use the BillingSystem by
 * importing our Spring configuration and simply autowiring this class.
 * 
 */
public class BillingSystem {

    private final Biller biller;
    private final CallTracker callTracker;

    private static final Logger LOG = LogManager.getLogger(BillingSystem.class);

    @Autowired
    public BillingSystem(final Biller biller, final CallTracker callTracker) {
	this.biller = biller;
	this.callTracker = callTracker;
    }

    public void callInitiated(final String caller, final String callee) {
	try {
	    callTracker.callInitiated(caller, callee);
	}
	catch (final IllegalCallException e) {
	    LOG.error(String.format("Call between %s and %s could not have been initated", caller, callee), e.getCause());
	}
    }

    public void callCompleted(final String caller, final String callee) {
	try {
	    callTracker.callCompleted(caller, callee);
	}
	catch (final IllegalCallException e) {
	    LOG.error(String.format("Call between %s and %s could not have been completed", caller, callee), e.getCause());
	}
    }

    public void createCustomerBills() {
	biller.createCustomerBills();
    }

}
