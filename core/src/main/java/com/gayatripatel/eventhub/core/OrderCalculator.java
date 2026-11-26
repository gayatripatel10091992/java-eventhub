package com.gayatripatel.eventhub.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Prices an order for one ticket tier. Rules: a group discount for 5+ tickets, a per-order booking fee
 * (waived for free tickets) and sales tax on the discounted subtotal plus fee.
 */
public final class OrderCalculator {

    public static final int GROUP_SIZE = 5;
    public static final int GROUP_DISCOUNT_PERCENT = 10;
    public static final long MIN_FEE_CENTS = 99;
    public static final int FEE_PERCENT = 4;
    public static final double TAX_PERCENT = 8.875;

    private OrderCalculator() {
    }

    public static final class Line {
        public final String label;
        public final long cents;

        Line(String label, long cents) {
            this.label = label;
            this.cents = cents;
        }
    }

    public static final class Summary {
        public final List<Line> lines;
        public final long totalCents;

        Summary(List<Line> lines, long totalCents) {
            this.lines = Collections.unmodifiableList(lines);
            this.totalCents = totalCents;
        }
    }

    /** Null when the quantity is valid, otherwise a message for the user. */
    public static String validate(TicketTier tier, int quantity) {
        if (tier == null) return "Choose a ticket type";
        if (tier.isSoldOut()) return tier.name + " is sold out";
        if (quantity < 1) return "Choose at least 1 ticket";
        if (quantity > tier.remaining) return "Only " + tier.remaining + " left";
        if (quantity > tier.maxPerOrder) return "Up to " + tier.maxPerOrder + " per order";
        return null;
    }

    public static Summary price(TicketTier tier, int quantity) {
        List<Line> lines = new ArrayList<>();
        long subtotal = tier.priceCents * quantity;
        lines.add(new Line(quantity + " × " + tier.name, subtotal));

        long discount = 0;
        if (quantity >= GROUP_SIZE && subtotal > 0) {
            discount = Math.round(subtotal * GROUP_DISCOUNT_PERCENT / 100.0);
            lines.add(new Line("Group discount (" + GROUP_DISCOUNT_PERCENT + "%)", -discount));
        }
        long afterDiscount = subtotal - discount;

        long fee = afterDiscount == 0 ? 0 : Math.max(MIN_FEE_CENTS, Math.round(afterDiscount * FEE_PERCENT / 100.0));
        if (fee > 0) lines.add(new Line("Booking fee", fee));

        long tax = Math.round((afterDiscount + fee) * TAX_PERCENT / 100.0);
        if (tax > 0) lines.add(new Line("Sales tax", tax));

        return new Summary(lines, afterDiscount + fee + tax);
    }
}
