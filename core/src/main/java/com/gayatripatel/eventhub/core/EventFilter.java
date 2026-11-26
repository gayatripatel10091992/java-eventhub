package com.gayatripatel.eventhub.core;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class EventFilter {

    public enum When { ANY, TODAY, THIS_WEEKEND, NEXT_7_DAYS }

    public final String query;
    public final Category category;
    public final When when;
    public final boolean freeOnly;

    public EventFilter(String query, Category category, When when, boolean freeOnly) {
        this.query = query == null ? "" : query.trim();
        this.category = category;
        this.when = when == null ? When.ANY : when;
        this.freeOnly = freeOnly;
    }

    public static EventFilter none() {
        return new EventFilter("", null, When.ANY, false);
    }

    public EventFilter withCategory(Category c) {
        return new EventFilter(query, c, when, freeOnly);
    }

    public EventFilter withQuery(String q) {
        return new EventFilter(q, category, when, freeOnly);
    }

    /** Upcoming (not yet ended) events that match, soonest first. */
    public List<Event> apply(List<Event> events, long nowMillis, ZoneId zone) {
        String[] terms = query.toLowerCase(Locale.ROOT).split("\\s+");
        LocalDate today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate();
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (e.endsAtMillis < nowMillis) continue;
            if (category != null && e.category != category) continue;
            if (freeOnly && (e.cheapestAvailableTier() == null || !e.cheapestAvailableTier().isFree())) continue;
            if (!matchesWhen(e, today, zone)) continue;
            String haystack = (e.title + " " + e.venue + " " + e.city + " " + e.organizer + " " + e.category.label).toLowerCase(Locale.ROOT);
            boolean all = true;
            for (String t : terms) {
                if (!t.isEmpty() && !haystack.contains(t)) {
                    all = false;
                    break;
                }
            }
            if (all) result.add(e);
        }
        Collections.sort(result, (a, b) -> Long.compare(a.startsAtMillis, b.startsAtMillis));
        return result;
    }

    private boolean matchesWhen(Event e, LocalDate today, ZoneId zone) {
        LocalDate day = Instant.ofEpochMilli(e.startsAtMillis).atZone(zone).toLocalDate();
        switch (when) {
            case TODAY:
                return day.equals(today);
            case THIS_WEEKEND: {
                int toSaturday = (6 - today.getDayOfWeek().getValue() + 7) % 7;
                LocalDate saturday = today.plusDays(toSaturday);
                return !day.isBefore(saturday) && !day.isAfter(saturday.plusDays(1))
                        || (today.getDayOfWeek().getValue() == 7 && day.equals(today));
            }
            case NEXT_7_DAYS:
                return !day.isBefore(today) && day.isBefore(today.plusDays(7));
            default:
                return true;
        }
    }
}
