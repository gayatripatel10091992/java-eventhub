package com.gayatripatel.eventhub;

import android.app.Application;
import android.view.ViewGroup;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.gayatripatel.eventhub.core.SampleEvents;
import com.gayatripatel.eventhub.data.ServiceLocator;
import com.gayatripatel.eventhub.ui.EventDetailActivity;
import com.gayatripatel.eventhub.ui.EventListActivity;
import com.gayatripatel.eventhub.ui.TicketsActivity;
import com.google.android.material.chip.ChipGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

@RunWith(AndroidJUnit4.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = 34, qualifiers = "w411dp-h891dp-xhdpi", application = Application.class)
public class ScreenshotTest {
    private FakeRepository repository;

    @Before
    public void setUp() {
        repository = new FakeRepository();
        ServiceLocator.override(repository, () -> SampleEvents.FIXED_NOW, SampleEvents.ZONE);
    }

    @Test
    public void eventList() {
        EventListActivity a = Robolectric.buildActivity(EventListActivity.class).setup().get();
        Screenshots.capture(a, "01_events");
    }

    @Test
    @Config(qualifiers = "+night")
    public void eventListDark() {
        EventListActivity a = Robolectric.buildActivity(EventListActivity.class).setup().get();
        Screenshots.capture(a, "02_events_dark");
    }

    @Test
    public void eventListFilteredByCategory() {
        EventListActivity a = Robolectric.buildActivity(EventListActivity.class).setup().get();
        ChipGroup chips = a.findViewById(R.id.category_chips);
        chips.check(chips.getChildAt(5).getId()); // All, Music, Tech, Food, Sports, Arts
        Screenshots.capture(a, "03_events_arts");
    }

    @Test
    public void eventDetailWithTableSelected() {
        EventDetailActivity a = Robolectric.buildActivity(EventDetailActivity.class,
                EventDetailActivity.intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), "e1")).setup().get();
        ViewGroup tiers = a.findViewById(R.id.tiers);
        tiers.getChildAt(1).performClick();
        a.findViewById(R.id.plus).performClick();
        Screenshots.capture(a, "04_event_detail");
    }

    @Test
    public void eventDetailGroupDiscount() {
        EventDetailActivity a = Robolectric.buildActivity(EventDetailActivity.class,
                EventDetailActivity.intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), "e4")).setup().get();
        for (int i = 0; i < 5; i++) a.findViewById(R.id.plus).performClick();
        Screenshots.capture(a, "05_group_discount");
    }

    @Test
    public void eventDetailOrderLimit() {
        EventDetailActivity a = Robolectric.buildActivity(EventDetailActivity.class,
                EventDetailActivity.intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), "e6")).setup().get();
        for (int i = 0; i < 4; i++) a.findViewById(R.id.plus).performClick();
        Screenshots.capture(a, "06_order_limit");
    }

    @Test
    public void tickets() {
        repository.seedTickets();
        TicketsActivity a = Robolectric.buildActivity(TicketsActivity.class).setup().get();
        Screenshots.capture(a, "07_my_tickets");
    }
}
