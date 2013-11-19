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
	super();
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
	this.printer = HtmlPrinter.getInstance();
    }

    // TODO this method should be renamed but stub left for legacy reasons?
    public void send(final Customer customer, final List<BillingSystem.LineItem> calls, final String totalBill) {
	this.printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
	for (final BillingSystem.LineItem call : calls) {
	    this.printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
	    // TODO MoneyFromatter should be injected?
	}
	this.printer.printTotal(totalBill);
    }

}