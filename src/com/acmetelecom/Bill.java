package com.acmetelecom;

import java.math.BigDecimal;
import java.util.List;

import com.acmetelecom.BillingSystem.LineItem;
import com.acmetelecom.customer.Customer;

public class Bill {
    private final Customer customer;
    private final List<LineItem> records;
    private final BigDecimal total;

    public Bill(final Customer customer, final List<LineItem> records, final BigDecimal total) {
	this.customer = customer;
	this.records = records;
	this.total = total;
    }

    public Customer getCustomer() {
	return customer;
    }

    public List<LineItem> getRecords() {
	return records;
    }

    public BigDecimal getTotal() {
	return total;
    }
}
