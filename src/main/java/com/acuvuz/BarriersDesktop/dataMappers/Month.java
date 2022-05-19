package com.acuvuz.BarriersDesktop.dataMappers;

import java.time.format.TextStyle;
import java.util.Locale;

public class Month {
    private final java.time.Month month;
    Month(java.time.Month monthObj) {
        month = monthObj;
    }
    public String getDisplayName() {
        return month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
    }

    public int getMaxLength() {
        return month.maxLength();
    }

    public int length(boolean leapYear) {
        return month.length(leapYear);
    }

}
