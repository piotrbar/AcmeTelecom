package com.acmetelecom;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.acmetelecom.customer.Customer;

/**
 * Provides functionality for generating/printing bills.
 */
public class BillGenerator {

    // Printer used for generating the bill
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

    /**
     * Formats the bill according to the printer standards and prints it.
     */
    public void send(final Customer customer, final List<BillingSystem.LineItem> calls, final String totalBill) {
	// Format the bill nicely
	printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
	// Print the price and duration for each call on a separate line
	for (final BillingSystem.LineItem call : calls) {
	    printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
	}
	// The total damage
	printer.printTotal(totalBill);
    }

}