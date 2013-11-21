package com.acmetelecom.util;


/**
 * Prints HTML to the console.
 */
public class HtmlPrinter implements Printer {

    private static Printer instance = new HtmlPrinter();

    /**
     * Singleton pattern.
     */
    private HtmlPrinter() {
    }

    public static Printer getInstance() {
	return instance;
    }

    @Override
    public void printHeading(final String name, final String phoneNumber, final String pricePlan) {
	beginHtml();
	System.out.println(h2(name + "/" + phoneNumber + " - " + "Price Plan: " + pricePlan));
	beginTable();
    }

    /**
     * Begin HTML table.
     */
    private void beginTable() {
	System.out.println("<table border=\"1\">");
	System.out.println(tr(th("Time") + th("Number") + th("Duration") + th("Cost")));
    }

    /**
     * End HTML table.
     */
    private void endTable() {
	System.out.println("</table>");
    }

    /**
     * Print HTML heading.
     */
    private String h2(final String text) {
	return "<h2>" + text + "</h2>";
    }

    @Override
    public void printItem(final String time, final String callee, final String duration, final String cost) {
	System.out.println(tr(td(time) + td(callee) + td(duration) + td(cost)));
    }

    /**
     * Print table row.
     */
    private String tr(final String text) {
	return "<tr>" + text + "</tr>";
    }

    /**
     * Print table heading.
     */
    private String th(final String text) {
	return "<th width=\"160\">" + text + "</th>";
    }

    /**
     * Print table column.
     */
    private String td(final String text) {
	return "<td>" + text + "</td>";
    }

    @Override
    public void printTotal(final String total) {
	endTable();
	System.out.println(h2("Total: " + total));
	endHtml();
    }

    /**
     * Annotates an HTML page.
     */
    private void beginHtml() {
	System.out.println("<html>");
	System.out.println("<head></head>");
	System.out.println("<body>");
	System.out.println("<h1>");
	System.out.println("Acme Telecom");
	System.out.println("</h1>");
    }

    private void endHtml() {
	System.out.println("</body>");
	System.out.println("</html>");
    }
}
