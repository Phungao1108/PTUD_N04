package com.team.invoice.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat PERIOD_FORMAT = new SimpleDateFormat("MM/yyyy");

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DISPLAY_DATE.format(date);
    }
}
