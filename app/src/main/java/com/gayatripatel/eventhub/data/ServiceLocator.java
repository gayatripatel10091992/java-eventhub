package com.gayatripatel.eventhub.data;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import com.gayatripatel.eventhub.BuildConfig;
import java.time.ZoneId;

/** Tiny manual DI container. Tests swap in fakes before launching an activity. */
public final class ServiceLocator {
    private static volatile EventRepository repository;
    private static volatile Clock clock = System::currentTimeMillis;
    private static volatile ZoneId zone = ZoneId.systemDefault();

    private ServiceLocator() {
    }

    public static EventRepository repository(Context context) {
        EventRepository r = repository;
        if (r == null) {
            synchronized (ServiceLocator.class) {
                r = repository;
                if (r == null) {
                    r = new DefaultEventRepository(ApiClient.create(clock, BuildConfig.DEBUG), AppDatabase.get(context).ticketDao());
                    repository = r;
                }
            }
        }
        return r;
    }

    public static Clock clock() {
        return clock;
    }

    public static ZoneId zone() {
        return zone;
    }

    @VisibleForTesting
    public static void override(EventRepository fake, Clock fixedClock, ZoneId fixedZone) {
        repository = fake;
        clock = fixedClock;
        zone = fixedZone;
    }
}
