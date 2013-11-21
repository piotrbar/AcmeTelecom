package com.acmetelecom;

/**
 * Prints HTML to a file.
 */
public class FilePrinter implements Printer {

    private static Printer instance = new FilePrinter();
    private final StringBuilder billBuilder;

    /**
     * Singleton pattern.
     */
    private FilePrinter() {
	billBuilder = new StringBuilder();
    }

    public static Printer getInstance() {
	return instance;
    }

    @Override
    public void printHeading(final String name, final String phoneNumber, final String pricePlan) {
	beginHtml();
	billBuilder.append(h2(name + "/" + phoneNumber + " - " + "Price Plan: " + pricePlan) + "\n");
	beginTable();
    }

    /**
     * Begin HTML table.
     */
    private void beginTable() {
	billBuilder.append("<table border=\"1\">\n");
	billBuilder.append(tr(th("Time") + th("Number") + th("Duration") + th("Cost")) + "\n");
    }

    /**
     * End HTML table.
     */
    private void endTable() {
	billBuilder.append("</table>\n");
    }

    /**
     * Print HTML heading.
     */
    private String h2(final String text) {
	return "<h2>" + text + "</h2>";
    }

    @Override
    public void printItem(final String time, final String callee, final String duration, final String cost) {
	billBuilder.append(tr(td(time) + td(callee) + td(duration) + td(cost)) + "\n");
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
     * Print column.
     */
    private String td(final String text) {
	return "<td>" + text + "</td>";
    }

    @Override
    public void printTotal(final String total) {
	endTable();
	billBuilder.append(h2("Total: " + total) + "\n");
	endHtml();
    }

    /**
     * Annotates an HTML page.
     */
    private void beginHtml() {
	billBuilder.append("<html>\n");
	billBuilder.append("<head></head>\n");
	billBuilder.append("<body>\n");
	billBuilder.append("<h1>\n");
	billBuilder.append("Acme Telecom\n");
	billBuilder.append("</h1>\n");
    }

    private void endHtml() {
	billBuilder.append("</body>\n");
	billBuilder.append("</html>\n");
    }

    public String getBill() {
	return billBuilder.toString();
    }

    public void flush() {
	billBuilder.setLength(0);
    }

}
