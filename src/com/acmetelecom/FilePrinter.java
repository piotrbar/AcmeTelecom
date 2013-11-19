package com.acmetelecom;

public class FilePrinter implements Printer {

    private static Printer instance = new FilePrinter();
    private final StringBuilder billBuilder;

    private FilePrinter() {
	this.billBuilder = new StringBuilder();
    }

    public static Printer getInstance() {
	return instance;
    }

    @Override
    public void printHeading(final String name, final String phoneNumber, final String pricePlan) {
	this.beginHtml();
	this.billBuilder.append(this.h2(name + "/" + phoneNumber + " - " + "Price Plan: " + pricePlan) + "\n");
	this.beginTable();
    }

    private void beginTable() {
	this.billBuilder.append("<table border=\"1\">\n");
	this.billBuilder.append(this.tr(this.th("Time") + this.th("Number") + this.th("Duration") + this.th("Cost")) + "\n");
    }

    private void endTable() {
	this.billBuilder.append("</table>\n");
    }

    private String h2(final String text) {
	return "<h2>" + text + "</h2>";
    }

    @Override
    public void printItem(final String time, final String callee, final String duration, final String cost) {
	this.billBuilder.append(this.tr(this.td(time) + this.td(callee) + this.td(duration) + this.td(cost)) + "\n");
    }

    private String tr(final String text) {
	return "<tr>" + text + "</tr>";
    }

    private String th(final String text) {
	return "<th width=\"160\">" + text + "</th>";
    }

    private String td(final String text) {
	return "<td>" + text + "</td>";
    }

    @Override
    public void printTotal(final String total) {
	this.endTable();
	this.billBuilder.append(this.h2("Total: " + total) + "\n");
	this.endHtml();
    }

    private void beginHtml() {
	this.billBuilder.append("<html>\n");
	this.billBuilder.append("<head></head>\n");
	this.billBuilder.append("<body>\n");
	this.billBuilder.append("<h1>\n");
	this.billBuilder.append("Acme Telecom\n");
	this.billBuilder.append("</h1>\n");
    }

    private void endHtml() {
	this.billBuilder.append("</body>\n");
	this.billBuilder.append("</html>\n");
    }

    public String getBill() {
	return this.billBuilder.toString();
    }

    public void flush() {
	this.billBuilder.setLength(0);
    }

}
