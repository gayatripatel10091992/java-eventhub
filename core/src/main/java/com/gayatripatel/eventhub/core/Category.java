package com.gayatripatel.eventhub.core;

public enum Category {
    MUSIC("Music", 0xFF7C3AED),
    TECH("Tech", 0xFF2563EB),
    FOOD("Food & drink", 0xFFEA580C),
    SPORTS("Sports", 0xFF16A34A),
    ARTS("Arts", 0xFFDB2777),
    HEALTH("Health", 0xFF0D9488);

    public final String label;
    /** ARGB colour used for the generated event banner. */
    public final int color;

    Category(String label, int color) {
        this.label = label;
        this.color = color;
    }
}
