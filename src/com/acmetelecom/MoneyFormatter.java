package com.acmetelecom;

import java.math.BigDecimal;

/**
 * Utility class for formatting currency.
 */
class MoneyFormatter {
    public static String penceToPounds(final BigDecimal pence) {
	BigDecimal pounds = pence.divide(new BigDecimal(100));
	return String.format("%.2f", pounds.doubleValue());
    }
}
