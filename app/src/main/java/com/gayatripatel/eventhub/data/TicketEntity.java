package com.gayatripatel.eventhub.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.gayatripatel.eventhub.core.Ticket;

@Entity(tableName = "tickets")
public class TicketEntity {
    @PrimaryKey
    @NonNull
    public String bookingRef = "";
    public String eventId;
    public String eventTitle;
    public long startsAtMillis;
    public String venue;
    public String tierName;
    public int quantity;
    public long totalCents;
    public String holderName;

    public static TicketEntity from(Ticket t) {
        TicketEntity e = new TicketEntity();
        e.bookingRef = t.bookingRef;
        e.eventId = t.eventId;
        e.eventTitle = t.eventTitle;
        e.startsAtMillis = t.startsAtMillis;
        e.venue = t.venue;
        e.tierName = t.tierName;
        e.quantity = t.quantity;
        e.totalCents = t.totalCents;
        e.holderName = t.holderName;
        return e;
    }

    public Ticket toTicket() {
        return new Ticket(bookingRef, eventId, eventTitle, startsAtMillis, venue, tierName, quantity, totalCents, holderName);
    }
}
