package com.gayatripatel.eventhub.core;

public final class Ticket {
    public final String bookingRef;
    public final String eventId;
    public final String eventTitle;
    public final long startsAtMillis;
    public final String venue;
    public final String tierName;
    public final int quantity;
    public final long totalCents;
    public final String holderName;

    public Ticket(String bookingRef, String eventId, String eventTitle, long startsAtMillis, String venue,
                  String tierName, int quantity, long totalCents, String holderName) {
        this.bookingRef = bookingRef;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.startsAtMillis = startsAtMillis;
        this.venue = venue;
        this.tierName = tierName;
        this.quantity = quantity;
        this.totalCents = totalCents;
        this.holderName = holderName;
    }

    /** Payload encoded into the ticket's QR code and checked at the door. */
    public String checkInPayload() {
        return CheckInCode.create(bookingRef, eventId, quantity);
    }
}
