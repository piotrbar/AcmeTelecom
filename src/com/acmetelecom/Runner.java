package com.acmetelecom;

public class Runner {

    public static void main(final String[] args) throws Exception {
	System.out.println("Running...");

	CallLog log = new ListCallLog();
	final BillingSystem billingSystem = new BillingSystem(new Biller(log), new CallTracker(log));

	billingSystem.callInitiated("447722113434", "447766511332");
	sleepSeconds(20);
	billingSystem.callCompleted("447722113434", "447766511332");

	billingSystem.callInitiated("447722113434", "447711111111");
	sleepSeconds(30);
	billingSystem.callCompleted("447722113434", "447711111111");

	billingSystem.callInitiated("447777765432", "447711111111");
	sleepSeconds(60);
	billingSystem.callCompleted("447777765432", "447711111111");

	billingSystem.createCustomerBills();
    }

    private static void sleepSeconds(final int n) throws InterruptedException {
	Thread.sleep(n * 1000);
    }

}