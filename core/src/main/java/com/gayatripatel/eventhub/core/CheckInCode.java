package com.gayatripatel.eventhub.core;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/**
 * QR payload for door check-in: {@code EVH1|ref|event|qty|crc}. The CRC catches damaged or hand-edited codes
 * offline; real tickets are also signed server-side.
 */
public final class CheckInCode {
    private static final String PREFIX = "EVH1";

    private CheckInCode() {
    }

    public static String create(String bookingRef, String eventId, int quantity) {
        String body = PREFIX + "|" + bookingRef + "|" + eventId + "|" + quantity;
        return body + "|" + checksum(body);
    }

    /** Returns the booking reference when the code is valid for this event, otherwise null. */
    public static String verify(String payload, String expectedEventId) {
        if (payload == null) return null;
        String[] parts = payload.split("\\|");
        if (parts.length != 5 || !PREFIX.equals(parts[0])) return null;
        String body = parts[0] + "|" + parts[1] + "|" + parts[2] + "|" + parts[3];
        if (!checksum(body).equals(parts[4])) return null;
        if (!parts[2].equals(expectedEventId)) return null;
        return parts[1];
    }

    private static String checksum(String body) {
        CRC32 crc = new CRC32();
        crc.update(body.getBytes(StandardCharsets.UTF_8));
        return Long.toString(crc.getValue(), 36).toUpperCase();
    }
}
