package com.acmetelecom;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Call {
    private final CallEvent start;
    private final CallEvent end;

    public Call(final CallEvent start, final CallEvent end) {
	this.start = start;
	this.end = end;
    }

    public String callee() {
	return start.getCallee();
    }

    public int durationSeconds() {
	return (int) (((end.time() - start.time()) / 1000));
    }

    public String date() {
	// return SimpleDateFormat.getInstance().format(new Date(start.time()));

	// Returns identical string to java.date as before
	final DateTimeFormatter dtf = DateTimeFormat.shortDateTime();
	return new DateTime(System.currentTimeMillis()).toString(dtf);

    }

    public DateTime startTime() {
	return new DateTime(start.time());
    }

    public DateTime endTime() {
	return new DateTime(end.time());
    }
}
