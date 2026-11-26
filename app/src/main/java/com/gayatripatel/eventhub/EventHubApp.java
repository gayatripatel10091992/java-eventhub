package com.gayatripatel.eventhub;

import android.app.Application;
import android.os.StrictMode;

public class EventHubApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        }
    }
}
