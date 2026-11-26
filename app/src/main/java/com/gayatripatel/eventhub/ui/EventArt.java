package com.gayatripatel.eventhub.ui;

import android.graphics.drawable.GradientDrawable;
import androidx.annotation.DrawableRes;
import androidx.core.graphics.ColorUtils;
import com.gayatripatel.eventhub.R;
import com.gayatripatel.eventhub.core.Category;

/** Generated banners per category, so the demo needs no image hosting and screenshots stay deterministic. */
final class EventArt {
    private EventArt() {
    }

    static GradientDrawable banner(Category category, String seed) {
        int base = category.color;
        int shift = (seed.hashCode() & 0x7fffffff) % 3;
        int darker = ColorUtils.blendARGB(base, 0xFF000000, 0.35f + 0.08f * shift);
        int lighter = ColorUtils.blendARGB(base, 0xFFFFFFFF, 0.12f * shift);
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{lighter, darker});
        d.setShape(GradientDrawable.RECTANGLE);
        return d;
    }

    @DrawableRes
    static int glyph(Category category) {
        switch (category) {
            case MUSIC:
                return R.drawable.ic_cat_music;
            case TECH:
                return R.drawable.ic_cat_tech;
            case FOOD:
                return R.drawable.ic_cat_food;
            case SPORTS:
                return R.drawable.ic_cat_sports;
            case ARTS:
                return R.drawable.ic_cat_arts;
            default:
                return R.drawable.ic_cat_health;
        }
    }
}
