package com.gayatripatel.eventhub.core;

import java.util.Collections;
import java.util.List;

/** Plain model shared by the API layer (Gson), the UI and the tests. */
public final class Event {
    public final String id;
    public final String title;
    public final Category category;
    public final String venue;
    public final String city;
    public final long startsAtMillis;
    public final long endsAtMillis;
    public final String organizer;
    public final String description;
    public final List<TicketTier> tiers;

    public Event(String id, String title, Category category, String venue, String city, long startsAtMillis,
                 long endsAtMillis, String organizer, String description, List<TicketTier> tiers) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.venue = venue;
        this.city = city;
        this.startsAtMillis = startsAtMillis;
        this.endsAtMillis = endsAtMillis;
        this.organizer = organizer;
        this.description = description;
        this.tiers = tiers == null ? Collections.<TicketTier>emptyList() : Collections.unmodifiableList(tiers);
    }

    /** Cheapest tier that still has tickets, or null when everything is sold out. */
    public TicketTier cheapestAvailableTier() {
        TicketTier best = null;
        for (TicketTier t : tiers) {
            if (!t.isSoldOut() && (best == null || t.priceCents < best.priceCents)) best = t;
        }
        return best;
    }

    public int totalRemaining() {
        int sum = 0;
        for (TicketTier t : tiers) sum += Math.max(0, t.remaining);
        return sum;
    }

    public boolean isSellingFast() {
        int remaining = totalRemaining();
        return remaining > 0 && remaining <= 25;
    }

    public TicketTier tier(String tierId) {
        for (TicketTier t : tiers) if (t.id.equals(tierId)) return t;
        return null;
    }
}
