package com.acmetelecom.util;

import java.math.BigDecimal;

/**
 * Utility class for formatting currency.
 */
public class MoneyFormatter {
    public static String penceToPounds(final BigDecimal pence) {
	BigDecimal pounds = pence.divide(new BigDecimal(100));
	return String.format("%.2f", pounds.doubleValue());
    }
}
