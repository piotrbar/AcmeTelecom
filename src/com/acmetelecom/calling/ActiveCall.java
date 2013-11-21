package com.acmetelecom.calling;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A class encapsulating the behaviour of an active call. No end time, no
 * duration. Enforces type safety.
 */
public class ActiveCall extends Call {

    private final Long timeStart;

    /**
     * Active calls are constructed based on a caller, callee and time of call
     */
    public ActiveCall(final String caller, final String callee, final Long timeStart) {
	super(caller, callee);
	this.timeStart = timeStart;
    }

    @Override
    public String date() {
	final DateTimeFormatter dtf = DateTimeFormat.shortDateTime();
	return new DateTime(timeStart).toString(dtf);
    }

    @Override
    public Long startTime() {
	return timeStart;
    }

}
