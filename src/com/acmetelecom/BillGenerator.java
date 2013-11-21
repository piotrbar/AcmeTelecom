package com.acmetelecom;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.customer.Customer;

public class BillGenerator {

    private final Printer printer;

    /**
     * Construct the BillGenerator and use the given printer for output.
     * 
     * @param printer
     *            The printer used to output the generated bill.
     */
    @Autowired
    public BillGenerator(final Printer printer) {
	this.printer = printer;
    }

    /**
     * The constructor is left for legacy reasons and instructs the class to use
     * an HTMLPrinter by default.
     * 
     * @deprecated use {@link #BillGenerator(Printer)} instead.
     */
    @Deprecated
    public BillGenerator() {
	printer = HtmlPrinter.getInstance();
    }

    // TODO this method should be renamed but stub left for legacy reasons?
    public void send(final Customer customer, final List<BillingSystem.LineItem> calls, final String totalBill) {
	printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
	for (final BillingSystem.LineItem call : calls) {
	    printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
	    // TODO MoneyFromatter should be injected?
	}
	printer.printTotal(totalBill);
    }

    public void send(final Bill bill) {
	send(bill.getCustomer(), bill.getRecords(), MoneyFormatter.penceToPounds(bill.getTotal()));
    }

}