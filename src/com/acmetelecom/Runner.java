package com.acmetelecom;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Runner {

    private final static Logger LOG = LogManager.getLogger(Runner.class);

    public static void main(final String[] args) {
	LOG.info("Starting the application ...");
	final ApplicationContext context = new FileSystemXmlApplicationContext("/config/application.xml");
	LOG.info("Creating the billing system ...");
	final BillingSystem billingSystem = (BillingSystem) context.getBean("billingSystem");
    }

}
