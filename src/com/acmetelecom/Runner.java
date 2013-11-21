package com.acmetelecom;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Runner {

    private final static Logger LOG = LogManager.getLogger(Runner.class);

    public static void main(final String[] args) throws InterruptedException {
	LOG.info("Starting the application ...");
	final ApplicationContext context = new FileSystemXmlApplicationContext("/config/application.xml");

	LOG.info("Creating the billing system ...");
	final BillingSystem billingSystem = (BillingSystem) context.getBean("billingSystem");

	System.out.println("Running...");
	billingSystem.callInitiated("447722113434", "447766511332");
	sleepSeconds(20);
	billingSystem.callCompleted("447722113434", "447766511332");

	billingSystem.callInitiated("447722113434", "447711111111");
	sleepSeconds(30);
	billingSystem.callCompleted("447722113434", "447711111111");

	billingSystem.callInitiated("447777765432", "447711111111");
	sleepSeconds(60);
	billingSystem.callCompleted("447777765432", "447711111111");

	System.out.println("Generating customer bills...");
	billingSystem.createCustomerBills();
    }

    private static void sleepSeconds(final int n) throws InterruptedException {
	Thread.sleep(n * 1000);
    }

}
