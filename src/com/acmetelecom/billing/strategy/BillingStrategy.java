package com.acmetelecom.billing.strategy;

import com.acmetelecom.billing.Bill;
import com.acmetelecom.calling.FinishedCall;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;

public interface BillingStrategy {
    public Bill generateBill(Customer customer, Iterable<FinishedCall> calls, Tariff tariff);
}
