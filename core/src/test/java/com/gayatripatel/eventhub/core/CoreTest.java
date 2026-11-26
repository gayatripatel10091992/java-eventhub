package com.gayatripatel.eventhub.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class CoreTest {
    private final long now = SampleEvents.FIXED_NOW;
    private final List<Event> events = SampleEvents.events(now);

    private static List<String> ids(List<Event> list) {
        List<String> out = new ArrayList<>();
        for (Event e : list) out.add(e.id);
        return out;
    }

    @Test
    public void pastEventsAreHiddenAndResultsAreSoonestFirst() {
        List<Event> all = EventFilter.none().apply(events, now, SampleEvents.ZONE);
        assertEquals("[e2, e6, e1, e3, e5, e4]", ids(all).toString());
    }

    @Test
    public void filtersCombine() {
        assertEquals("[e5]", ids(EventFilter.none().withCategory(Category.ARTS).apply(events, now, SampleEvents.ZONE)).toString());
        assertEquals("[e2]", ids(new EventFilter("", null, EventFilter.When.ANY, true).apply(events, now, SampleEvents.ZONE)).toString());
        assertEquals("[e2, e4]", ids(EventFilter.none().withQuery("brooklyn").apply(events, now, SampleEvents.ZONE)).toString());
        assertEquals("[e2]", ids(new EventFilter("", null, EventFilter.When.TODAY, false).apply(events, now, SampleEvents.ZONE)).toString());
        // Friday → the weekend is Sat 21 + Sun 22: the yoga class and the jazz night; the food fest is on Monday.
        assertEquals("[e6, e1]", ids(new EventFilter("", null, EventFilter.When.THIS_WEEKEND, false).apply(events, now, SampleEvents.ZONE)).toString());
    }

    @Test
    public void cheapestTierSkipsSoldOut() {
        Event food = events.get(2);
        assertEquals("entry", food.cheapestAvailableTier().id);
        assertTrue(food.tier("tasting").isSoldOut());
        assertTrue(events.get(4).isSellingFast());
        assertFalse(events.get(0).isSellingFast());
    }

    @Test
    public void orderValidation() {
        TicketTier table = events.get(0).tier("table");
        assertNull(OrderCalculator.validate(table, 2));
        assertEquals("Up to 2 per order", OrderCalculator.validate(table, 3));
        assertEquals("Only 9 left", OrderCalculator.validate(events.get(5).tiers.get(0), 10));
        assertEquals("Tasting pass is sold out", OrderCalculator.validate(events.get(2).tier("tasting"), 1));
    }

    @Test
    public void pricingWithGroupDiscountFeeAndTax() {
        TicketTier ga = events.get(0).tier("ga");
        OrderCalculator.Summary two = OrderCalculator.price(ga, 2);
        // 2 × 35.00 = 70.00, fee max(0.99, 4% = 2.80) = 2.80, tax 8.875% of 72.80 = 6.46
        assertEquals(7_000 + 280 + 646, two.totalCents);

        OrderCalculator.Summary six = OrderCalculator.price(ga, 6);
        assertEquals(-2_100, six.lines.get(1).cents); // 10% of 210.00
        long sum = 0;
        for (OrderCalculator.Line l : six.lines) sum += l.cents;
        assertEquals(six.totalCents, sum);

        assertEquals(0, OrderCalculator.price(events.get(1).tiers.get(0), 1).totalCents);
    }

    @Test
    public void moneyAndDates() {
        assertEquals("Free", Money.format(0));
        assertEquals("$1,234.50", Money.format(123_450));
        assertEquals("Sun, Nov 22 · 8:00 PM", DateLabels.full(events.get(0).startsAtMillis, SampleEvents.ZONE));
        assertEquals("NOV", DateLabels.month(events.get(0).startsAtMillis, SampleEvents.ZONE));
        assertEquals("Starts in 9 hours", DateLabels.countdown(events.get(1).startsAtMillis, events.get(1).endsAtMillis, now, SampleEvents.ZONE));
        assertEquals("In 2 days", DateLabels.countdown(events.get(0).startsAtMillis, events.get(0).endsAtMillis, now, SampleEvents.ZONE));
        assertEquals("Ended", DateLabels.countdown(events.get(6).startsAtMillis, events.get(6).endsAtMillis, now, SampleEvents.ZONE));
    }

    @Test
    public void checkInCodesRoundTripAndRejectTampering() {
        String code = CheckInCode.create("EVH-7Q2K9", "e1", 2);
        assertEquals("EVH-7Q2K9", CheckInCode.verify(code, "e1"));
        assertNull(CheckInCode.verify(code, "e2"));
        assertNull(CheckInCode.verify(code.replace("|2|", "|9|"), "e1"));
        assertNull(CheckInCode.verify("garbage", "e1"));
    }
}
