package com.gayatripatel.eventhub.core;

import java.util.Locale;

public final class Money {
    private Money() {
    }

    public static String format(long cents) {
        if (cents == 0) return "Free";
        String sign = cents < 0 ? "-" : "";
        long abs = Math.abs(cents);
        return String.format(Locale.US, "%s$%,d.%02d", sign, abs / 100, abs % 100);
    }
}
