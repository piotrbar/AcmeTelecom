package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.BillingSystem.LineItem;
import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

public class Biller {

    private final CallLog callLog;
    private final TariffLibrary tariffLibrary;
    private final CustomerDatabase customerDatabase;
    private final BillGenerator billGenerator;
    private final CallCostCalculator callCostCalculator;

    @Autowired
    public Biller(final CallLog callLog, final TariffLibrary tariffLibrary, final CustomerDatabase customerDatabase, final BillGenerator billGenerator,
	    final CallCostCalculator callCostCalculator) {
	this.callLog = callLog;
	this.tariffLibrary = tariffLibrary;
	this.customerDatabase = customerDatabase;
	this.billGenerator = billGenerator;
	this.callCostCalculator = callCostCalculator;
    }

    public Biller(final CallLog callLog, final BillGenerator billGenerator, final CallCostCalculator callCostCalculator) {
	this.callLog = callLog;
	this.billGenerator = billGenerator;
	this.callCostCalculator = callCostCalculator;
	tariffLibrary = CentralTariffDatabase.getInstance();
	customerDatabase = CentralCustomerDatabase.getInstance();
    }

    public void createCustomerBills() {
	final List<Customer> customers = customerDatabase.getCustomers();
	for (final Customer customer : customers) {
	    createBillFor(customer);
	}
	callLog.clearCompletedCalls();
    }

    private void createBillFor(final Customer customer) {
	final Iterable<Call> calls = callLog.getCallsForCustomer(customer.getPhoneNumber());

	BigDecimal totalBill = new BigDecimal(0);
	final List<LineItem> items = new ArrayList<LineItem>();

	for (final Call call : calls) {

	    final Tariff tariff = tariffLibrary.tarriffFor(customer);

	    BigDecimal cost = callCostCalculator.calculate(call, tariff);

	    cost = cost.setScale(0, RoundingMode.HALF_UP);
	    final BigDecimal callCost = cost;
	    totalBill = totalBill.add(callCost);
	    items.add(new LineItem(call, callCost));
	}

	billGenerator.send(customer, items, MoneyFormatter.penceToPounds(totalBill));
    }
}
