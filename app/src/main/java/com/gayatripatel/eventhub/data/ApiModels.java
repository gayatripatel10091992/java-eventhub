package com.gayatripatel.eventhub.data;

/** Request and response bodies for the bookings endpoint. Events use the core {@code Event} model directly. */
public final class ApiModels {
    private ApiModels() {
    }

    public static final class BookingRequest {
        public final String eventId;
        public final String tierId;
        public final int quantity;
        public final String holderName;

        public BookingRequest(String eventId, String tierId, int quantity, String holderName) {
            this.eventId = eventId;
            this.tierId = tierId;
            this.quantity = quantity;
            this.holderName = holderName;
        }
    }

    public static final class BookingResponse {
        public final String bookingRef;
        public final long totalCents;

        public BookingResponse(String bookingRef, long totalCents) {
            this.bookingRef = bookingRef;
            this.totalCents = totalCents;
        }
    }

    public static final class ErrorResponse {
        public final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
