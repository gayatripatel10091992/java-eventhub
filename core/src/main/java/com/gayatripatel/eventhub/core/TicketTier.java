package com.gayatripatel.eventhub.core;

public final class TicketTier {
    public final String id;
    public final String name;
    public final long priceCents;
    public final int remaining;
    public final int maxPerOrder;
    public final String perks;

    public TicketTier(String id, String name, long priceCents, int remaining, int maxPerOrder, String perks) {
        this.id = id;
        this.name = name;
        this.priceCents = priceCents;
        this.remaining = remaining;
        this.maxPerOrder = maxPerOrder;
        this.perks = perks;
    }

    public boolean isSoldOut() {
        return remaining <= 0;
    }

    public boolean isFree() {
        return priceCents == 0;
    }
}
