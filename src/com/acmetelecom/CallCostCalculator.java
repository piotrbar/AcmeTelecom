package com.acmetelecom;

import java.math.BigDecimal;

import com.acmetelecom.customer.Tariff;

public class CallCostCalculator {

    public BigDecimal calculate(final Call call, final Tariff tariff) {

	final BigDecimal cost;

	final DaytimePeakPeriod peakPeriod = new DaytimePeakPeriod();
	// TODO Why does the call have to be less than 12hrs?
	// cos otherwise it goes into peak time and charges peak

	final int noOfPeakSeconds = peakPeriod.getPeakSeconds(call.startTime(), call.endTime());

	final BigDecimal peakCost = new BigDecimal(noOfPeakSeconds).multiply(tariff.peakRate());
	final BigDecimal offPeakCost = new BigDecimal(call.durationSeconds() - noOfPeakSeconds).multiply(tariff.offPeakRate());

	System.out.println("peak cost is " + peakCost.toString());
	System.out.println("offpeak cost is" + offPeakCost.toString());
	// cost = cost.add(new BigDecimal(call.durationSeconds() -
	// noOfPeakSeconds)).multiply(tariff.offPeakRate());

	System.out.println("noOfPeakSecs is " + noOfPeakSeconds);
	System.out.println("duration of call is " + call.durationSeconds());
	System.out.println("tariff rate off peak is " + tariff.offPeakRate().toString());
	System.out.println("on peak rate is " + tariff.peakRate().toString());
	// System.out.println("cost is " + cost.toString());
	return peakCost.add(offPeakCost);
    }

}
