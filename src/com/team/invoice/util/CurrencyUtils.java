package com.team.invoice.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    private static final NumberFormat FORMATTER = NumberFormat.getInstance(new Locale("vi", "VN"));

    public static String formatMoney(double value) {
        return FORMATTER.format(Math.round(value));
    }

    public static String formatSummary(double value) {
        return String.format(Locale.US, "%.1ftr VNĐ", value / 1000000.0);
    }
}
