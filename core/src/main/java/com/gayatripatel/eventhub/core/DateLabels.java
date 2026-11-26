package com.gayatripatel.eventhub.core;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class DateLabels {
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a", Locale.US);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMM", Locale.US);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("d", Locale.US);

    private DateLabels() {
    }

    public static String full(long millis, ZoneId zone) {
        return FULL.format(at(millis, zone));
    }

    /** "NOV" for the date badge on event cards. */
    public static String month(long millis, ZoneId zone) {
        return MONTH.format(at(millis, zone)).toUpperCase(Locale.US);
    }

    public static String day(long millis, ZoneId zone) {
        return DAY.format(at(millis, zone));
    }

    public static String countdown(long startsAt, long endsAt, long now, ZoneId zone) {
        if (now >= endsAt) return "Ended";
        if (now >= startsAt) return "Happening now";
        long days = ChronoUnit.DAYS.between(at(now, zone).toLocalDate(), at(startsAt, zone).toLocalDate());
        if (days == 0) {
            long hours = Math.max(1, (startsAt - now) / 3_600_000L);
            return "Starts in " + hours + (hours == 1 ? " hour" : " hours");
        }
        if (days == 1) return "Tomorrow";
        return "In " + days + " days";
    }

    private static ZonedDateTime at(long millis, ZoneId zone) {
        return Instant.ofEpochMilli(millis).atZone(zone);
    }
}
