package com.gayatripatel.eventhub.core;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

/** Demo catalogue served by the app's mock API. Venues and organisers are fictional. */
public final class SampleEvents {
    public static final ZoneId ZONE = ZoneId.of("America/New_York");
    /** Friday 20 November 2026, 10:00. */
    public static final long FIXED_NOW = ZonedDateTime.of(2026, 11, 20, 10, 0, 0, 0, ZONE).toInstant().toEpochMilli();

    private static final long HOUR = 3_600_000L;
    private static final long DAY = 24 * HOUR;

    private SampleEvents() {
    }

    public static List<Event> events(long now) {
        long today = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(now), ZONE).toLocalDate().atStartOfDay(ZONE).toInstant().toEpochMilli();
        return Arrays.asList(
                new Event("e1", "Hudson Jazz Nights", Category.MUSIC, "Blue Pier Hall", "New York",
                        today + 2 * DAY + 20 * HOUR, today + 2 * DAY + 23 * HOUR, "Riverside Live",
                        "An evening of modern jazz on the river with three quartets and a late jam session.",
                        Arrays.asList(
                                new TicketTier("ga", "General admission", 3_500, 120, 8, "Standing"),
                                new TicketTier("table", "Table for two", 11_000, 6, 2, "Reserved table, 2 drinks"))),
                new Event("e2", "Android Dev Summit Meetup", Category.TECH, "Foundry Co-working", "Brooklyn",
                        today + 19 * HOUR, today + 21 * HOUR + 30 * 60_000L, "Brooklyn Mobile Devs",
                        "Talks on Jetpack Compose performance and Kotlin Multiplatform, then pizza and networking.",
                        Arrays.asList(new TicketTier("free", "RSVP", 0, 18, 1, "Free entry"))),
                new Event("e3", "Night Market Street Food Fest", Category.FOOD, "Pier 17 Plaza", "New York",
                        today + 3 * DAY + 17 * HOUR, today + 3 * DAY + 23 * HOUR, "Eat Local NYC",
                        "Forty vendors, live DJs and a dumpling-eating contest.",
                        Arrays.asList(
                                new TicketTier("entry", "Entry", 1_200, 640, 10, "Entry only"),
                                new TicketTier("tasting", "Tasting pass", 4_500, 0, 4, "10 tasting tokens"))),
                new Event("e4", "Sunrise 10K Run", Category.SPORTS, "Prospect Park", "Brooklyn",
                        today + 9 * DAY + 7 * HOUR, today + 9 * DAY + 11 * HOUR, "Run Brooklyn",
                        "Chip-timed 10K loop with medals for every finisher and a 5K option.",
                        Arrays.asList(
                                new TicketTier("10k", "10K entry", 4_000, 210, 6, "Timing chip, medal, shirt"),
                                new TicketTier("5k", "5K entry", 2_500, 150, 6, "Timing chip, medal"))),
                new Event("e5", "Immersive Light Gallery", Category.ARTS, "Hall of Light", "Queens",
                        today + 4 * DAY + 11 * HOUR, today + 4 * DAY + 20 * HOUR, "Lumen Arts",
                        "Walk through twelve rooms of projection art set to an original score.",
                        Arrays.asList(new TicketTier("timed", "Timed entry", 2_900, 22, 6, "60-minute slot"))),
                new Event("e6", "Mindful Mornings Yoga", Category.HEALTH, "Rooftop at The Line", "New York",
                        today + 2 * DAY + 8 * HOUR, today + 2 * DAY + 9 * HOUR + 15 * 60_000L, "Calm City Studio",
                        "Rooftop vinyasa flow for all levels. Mats provided.",
                        Arrays.asList(new TicketTier("class", "Class pass", 1_800, 9, 4, "Mat included"))),
                new Event("e7", "Indie Film Shorts Night", Category.ARTS, "Metro Cinema", "Manhattan",
                        today - 2 * DAY + 19 * HOUR, today - 2 * DAY + 22 * HOUR, "Indie Reel",
                        "Eight short films and a director Q&A.",
                        Arrays.asList(new TicketTier("seat", "Seat", 1_500, 40, 4, "Assigned seat"))));
    }
}
